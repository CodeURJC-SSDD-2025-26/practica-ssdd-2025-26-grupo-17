package es.dawgroup17.nexgym.controller;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.dawgroup17.nexgym.model.Image;
import es.dawgroup17.nexgym.model.Session;
import es.dawgroup17.nexgym.model.Comment;
import es.dawgroup17.nexgym.model.GymClass;

import es.dawgroup17.nexgym.service.ClassService;
import es.dawgroup17.nexgym.service.ImageService;
import es.dawgroup17.nexgym.service.UserService;

@Controller
public class ClassController {

	@Autowired
	private ClassService classService;

	@Autowired
	private ImageService imageService;

	@Autowired
	private UserService userService;

	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal != null) {
			model.addAttribute("logged", true);
			model.addAttribute("userName", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));
		} else {
			model.addAttribute("logged", false);
		}
	}

	@GetMapping("/")
	public String homePage(Model model, Principal principal) {
		model.addAttribute("cssIndex", true);
		model.addAttribute("classes", classService.findAll());
		if (principal != null) {
			userService.findByEmail(principal.getName()).ifPresent(user -> {
				model.addAttribute("user", user);
			});
		}
		return "index";
	}

	@GetMapping("/class/{id}")
	public String showClasses(Model model, @PathVariable long id, Principal principal, HttpServletRequest request) {
		Optional<GymClass> gymClass = classService.findById(id);
		if (gymClass.isPresent()) {
			List<Comment> comments = gymClass.get().getComments();
			for (Comment comment : comments) {
				if (principal != null && comment.getAppUser() != null) {
					boolean isOwner = comment.getAppUser().getEmail().equals(principal.getName());
					comment.setIsOwner(isOwner);
				} else {
					comment.setIsOwner(false);
				}
			}
			List<Image> images = gymClass.get().getImages();
			if (images != null && !images.isEmpty()) {
				images.get(0).setIsFirst(true);
			}
			List<Session> sessions = gymClass.get().getSessions();
			if (principal != null) {
				userService.findByEmail(principal.getName()).ifPresent(user -> {
					for (Session s : sessions) {
						s.setSignedUp(user.getUserSessions().contains(s));
						// Check overlap: user has another session at the same date+time
						boolean conflict = user.getUserSessions().stream()
							.filter(us -> !us.equals(s))
							.anyMatch(us -> us.getDate().equals(s.getDate()));
						s.setHasConflict(conflict);
					}
				});
			}
			model.addAttribute("class", gymClass.get());
			model.addAttribute("classId", gymClass.get().getId());
			model.addAttribute("sessions", sessions);
			model.addAttribute("cssClass", true);
			return "class";
		} else {
			model.addAttribute("cssError", true);
			return "error";
		}
	}

	@PostMapping("/admin/classes/{id}/delete")
	public String removeClassById(Model model, @PathVariable long id) {
		Optional<GymClass> gymClass = classService.findById(id);
		if (gymClass.isPresent()) {
			classService.delete(id);
		}
		return "redirect:/admin";
	}

	@PostMapping("/admin/newClass")
	public String addNewClass(
			GymClass newClass,
			@RequestParam(value = "schedule-day[]", required = false) List<String> scheduleDays,
			@RequestParam(value = "schedule-time[]", required = false) List<String> scheduleTimes,
			@RequestParam(value = "imageFiles", required = false) MultipartFile[] images) throws IOException {

		newClass.setAllSessions(new ArrayList<>());
		addSessionsFromSchedule(newClass, scheduleDays, scheduleTimes);
		newClass.setImages(buildImagesFromUpload(images));

		classService.save(newClass);
		return "redirect:/admin";
	}

	@PostMapping("/admin/updateClass/{id}")
	public String updateClass(
			Model model,
			@PathVariable long id,
			GymClass gymClass,
			@RequestParam(value = "schedule-day[]", required = false) List<String> scheduleDays,
			@RequestParam(value = "schedule-time[]", required = false) List<String> scheduleTimes,
			@RequestParam(value = "imageFiles", required = false) MultipartFile[] images)
			throws IOException {

		Optional<GymClass> classOpt = classService.findById(id);

		GymClass existingClass = classOpt.get();
		existingClass.setName(gymClass.getName());
		existingClass.setCoachName(gymClass.getCoachName());
		existingClass.setDescription(gymClass.getDescription());

		// Correctly wipes sessions: removes from AppUser.userSessions (owner side)
		// first, flushes to DB, then clears the session rows — avoids FK constraint
		// errors
		classService.clearSessions(existingClass);

		addSessionsFromSchedule(existingClass, scheduleDays, scheduleTimes);

		List<Image> uploadedImages = buildImagesFromUpload(images);
		if (!uploadedImages.isEmpty()) {
			existingClass.getImages().clear();
			existingClass.getImages().addAll(uploadedImages);
		}

		classService.save(existingClass);
		return "redirect:/admin";
	}

	@GetMapping("/classes/load")
	public String loadClasses(
			@RequestParam(defaultValue = "0") int offset,
			@RequestParam(defaultValue = "6") int limit,
			Model model) {
		PageRequest pageRequest = PageRequest.of(offset / limit, limit);
		Page<GymClass> page = classService.findAllPage(pageRequest);
		List<GymClass> classes = page.getContent();
		for (GymClass gc : classes) {
			List<Image> imgs = gc.getImages();
			if (imgs != null && !imgs.isEmpty()) {
				imgs.get(0).setIsFirst(true);
			}
		}
		model.addAttribute("classes", classes);
		model.addAttribute("hasMore", page.hasNext());
		return "ajaxImagesClasses";
	}

	private void addSessionsFromSchedule(GymClass gymClass, List<String> scheduleDays, List<String> scheduleTimes) {
		if (scheduleDays == null || scheduleTimes == null) {
			return;
		}
		int rows = Math.min(scheduleDays.size(), scheduleTimes.size());
		for (int i = 0; i < rows; i++) {
			String day = scheduleDays.get(i);
			String time = scheduleTimes.get(i);
			if (day == null || day.isBlank() || time == null || time.isBlank()) {
				continue;
			}
			DayOfWeek dayOfWeek = parseDayOfWeek(day);
			if (dayOfWeek == null) {
				continue;
			}
			try {
				LocalTime localTime = LocalTime.parse(time);
				LocalDate sessionDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(dayOfWeek));
				LocalDateTime dateTime = LocalDateTime.of(sessionDate, localTime);
				gymClass.setSessions(new Session(dateTime, 20, gymClass));
			} catch (DateTimeParseException e) {
				// Ignore malformed rows
			}
		}
	}

	private List<Image> buildImagesFromUpload(MultipartFile[] images) throws IOException {
		ArrayList<Image> uploadedImages = new ArrayList<>();
		if (images == null) {
			return uploadedImages;
		}
		for (MultipartFile image : images) {
			if (image != null && !image.isEmpty()) {
				uploadedImages.add(imageService.createImage(image.getInputStream()));
			}
		}
		return uploadedImages;
	}

	private DayOfWeek parseDayOfWeek(String day) {
		String normalizedDay = day.trim().toUpperCase(Locale.ROOT);
		return switch (normalizedDay) {
			case "MONDAY" -> DayOfWeek.MONDAY;
			case "TUESDAY" -> DayOfWeek.TUESDAY;
			case "WEDNESDAY" -> DayOfWeek.WEDNESDAY;
			case "THURSDAY" -> DayOfWeek.THURSDAY;
			case "FRIDAY" -> DayOfWeek.FRIDAY;
			case "SATURDAY" -> DayOfWeek.SATURDAY;
			case "SUNDAY" -> DayOfWeek.SUNDAY;
			default -> null;
		};
	}
}