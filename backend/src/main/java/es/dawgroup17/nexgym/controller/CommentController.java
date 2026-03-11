package es.dawgroup17.nexgym.controller;

import java.io.IOException;
import java.security.Principal;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.dawgroup17.nexgym.model.AppUser;
import es.dawgroup17.nexgym.model.Comment;
import es.dawgroup17.nexgym.model.GymClass;
import es.dawgroup17.nexgym.service.ClassService;
import es.dawgroup17.nexgym.service.CommentService;
import es.dawgroup17.nexgym.service.UserService;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private ClassService classService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {

        // This is used for getting the user information in the header of the page, to
        // show the username and the admin options if the user is logged in and is an
        // admin.
        Principal principal = request.getUserPrincipal();

        if (principal != null) { // principal != null means that the user is logged in, so we can get the user
                                 // information.

            model.addAttribute("logged", true);
            model.addAttribute("userName", principal.getName());
            model.addAttribute("admin", request.isUserInRole("ADMIN"));

        } else {
            model.addAttribute("logged", false);
        }
    }

    // Method for adding a comment to a gym class
    @PostMapping("/class/{classId}/newcomment")
    public String newCommentProcess(Model model, @RequestParam int rating, @RequestParam String content,
            @PathVariable Long classId, Principal principal, RedirectAttributes redirectAttributes) throws IOException {

        Optional<AppUser> user = userService.findByEmail(principal.getName());
        Optional<GymClass> gymClass = classService.findById(classId);

        if (user.isPresent() && gymClass.isPresent()) {
            Comment comment = new Comment(user.get(), rating, content);
            comment.setGymClass(gymClass.get());
            gymClass.get().getComments().add(comment);
            commentService.save(comment);
        } else {
            throw new SecurityException("You must be logged in to add a comment.");
        }

        return "redirect:/class/" + classId;
    }

    // Method for deleting a comment from a gym class
    @PostMapping("/class/{classId}/comments/{commentId}/delete")
    public String deleteCommentProcess(@PathVariable Long classId, @PathVariable Long commentId,
            Principal principal, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {

        if (principal == null) {
            throw new SecurityException("You must be logged in");
        }

        Optional<Comment> comment = commentService.findById(commentId);
        Optional<GymClass> gymClass = classService.findById(classId);

        if (comment.isPresent() && gymClass.isPresent()) {
            Optional<AppUser> user = userService.findByEmail(principal.getName());
            if (user.isPresent() && comment.get().getAppUser().getId() == user.get().getId()) {
                // Remove from the class's comment list first
                gymClass.get().getComments().remove(comment.get());
                classService.save(gymClass.get());
                // Now safe to delete
                commentService.delete(commentId);
            } else {
                throw new SecurityException("You don't have permission to delete this comment.");
            }
        } else {
            return "error";
        }

        return "redirect:/class/" + classId;
    }

    // Method for editing a comment from a gym class
    @PostMapping("/class/{classId}/comments/{commentId}/edit")
    public String editCommentProcess(Model model, @PathVariable Long classId, @PathVariable Long commentId,
            @RequestParam int rating, @RequestParam String content, Principal principal) throws IOException {

        if (principal == null) {
            throw new SecurityException("You must be logged in.");
        }

        Optional<Comment> comment = commentService.findById(commentId);
        Optional<AppUser> user = userService.findByEmail(principal.getName());

        if (comment.isPresent() && user.isPresent()) {
            if (comment.get().getAppUser().getId() == user.get().getId()) {
                comment.get().setComment(content);
                comment.get().setRating(rating);
                commentService.save(comment.get());
            } else {
                throw new SecurityException("You don't have permission to edit this comment.");
            }
        } else {
            model.addAttribute("cssError", true);
            return "error";
        }

        return "redirect:/class/" + classId;
    }

}
