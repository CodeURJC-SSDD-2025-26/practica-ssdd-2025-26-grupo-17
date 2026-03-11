// Schedule builder functionality
const scheduleBuilder = document.getElementById("schedule-builder");
const existingSessionsContainer = document.getElementById("existing-sessions");

const dayNames = [
  "Sunday",
  "Monday",
  "Tuesday",
  "Wednesday",
  "Thursday",
  "Friday",
  "Saturday",
];

const normalizeTime = (value) => {
  if (!value) {
    return "";
  }

  const [hours = "", minutes = ""] = value.split(":");
  if (!hours || !minutes) {
    return "";
  }

  return `${hours.padStart(2, "0")}:${minutes.padStart(2, "0")}`;
};

const getDayAndTimeFromDateString = (dateString) => {
  if (!dateString) {
    return null;
  }

  const parsedDate = new Date(dateString);
  if (Number.isNaN(parsedDate.getTime())) {
    return null;
  }

  const day = dayNames[parsedDate.getDay()];
  const time = `${String(parsedDate.getHours()).padStart(2, "0")}:${String(parsedDate.getMinutes()).padStart(2, "0")}`;

  return { day, time };
};

const scheduleDayOptions = `
  <option value="" disabled selected>Day</option>
  <option>Monday</option>
  <option>Tuesday</option>
  <option>Wednesday</option>
  <option>Thursday</option>
  <option>Friday</option>
  <option>Saturday</option>
  <option>Sunday</option>
`;
const scheduleTimeOptions = `
  <option value="" disabled selected>Time</option>
  <option>06:00</option>
  <option>07:00</option>
  <option>08:00</option>
  <option>09:00</option>
  <option>10:00</option>
  <option>11:00</option>
  <option>12:00</option>
  <option>13:00</option>
  <option>14:00</option>
  <option>15:00</option>
  <option>16:00</option>
  <option>17:00</option>
  <option>18:00</option>
  <option>19:00</option>
  <option>20:00</option>
  <option>21:00</option>
  <option>22:00</option>
`;

const buildScheduleRow = (selectedDay = "", selectedTime = "") => {
  const row = document.createElement("div");
  row.className = "schedule-row";
  row.innerHTML = `
    <select name="schedule-day[]" aria-label="Select day" required>
      ${scheduleDayOptions}
    </select>
    <select name="schedule-time[]" aria-label="Select time" required>
      ${scheduleTimeOptions}
    </select>
    <button class="schedule-button schedule-add" type="button" aria-label="Add schedule">+</button>
    <button class="schedule-button schedule-remove" type="button" aria-label="Remove schedule">-</button>
  `;

  const daySelect = row.querySelector('select[name="schedule-day[]"]');
  const timeSelect = row.querySelector('select[name="schedule-time[]"]');

  if (selectedDay) {
    daySelect.value = selectedDay;
  }

  if (selectedTime) {
    timeSelect.value = normalizeTime(selectedTime);
  }

  return row;
};

const initializeScheduleRows = () => {
  if (!scheduleBuilder) {
    return;
  }

  scheduleBuilder.innerHTML = "";

  const existingSessionInputs = existingSessionsContainer
    ? Array.from(existingSessionsContainer.querySelectorAll(".existing-session"))
    : [];

  if (existingSessionInputs.length === 0) {
    scheduleBuilder.appendChild(buildScheduleRow());
    return;
  }

  existingSessionInputs.forEach((sessionInput) => {
    const dateString = sessionInput.dataset.date;
    const scheduleData = getDayAndTimeFromDateString(dateString);

    if (scheduleData) {
      scheduleBuilder.appendChild(buildScheduleRow(scheduleData.day, scheduleData.time));
    }
  });

  if (!scheduleBuilder.querySelector(".schedule-row")) {
    scheduleBuilder.appendChild(buildScheduleRow());
  }
};

initializeScheduleRows();

if (scheduleBuilder) {
  scheduleBuilder.addEventListener("click", (event) => {
    const addButton = event.target.closest(".schedule-add");
    const removeButton = event.target.closest(".schedule-remove");

    if (addButton) {
      scheduleBuilder.appendChild(buildScheduleRow());
      return;
    }

    if (removeButton) {
      const row = removeButton.closest(".schedule-row");
      if (row) {
        row.remove();
      }

      if (!scheduleBuilder.querySelector(".schedule-row")) {
        scheduleBuilder.appendChild(buildScheduleRow());
      }
    }
  });
}

// Multiple images preview functionality
const classPhotosInput = document.getElementById("class-photos");
const imagesPreview = document.getElementById("images-preview");
const selectedFiles = [];
const MAX_IMAGES = 4;

const syncInputFiles = () => {
  if (!classPhotosInput || typeof DataTransfer === "undefined") {
    return;
  }

  const dataTransfer = new DataTransfer();
  selectedFiles.forEach((file) => dataTransfer.items.add(file));
  classPhotosInput.files = dataTransfer.files;
};

if (classPhotosInput) {
  classPhotosInput.addEventListener("change", (event) => {
    const files = Array.from(event.target.files);
    const remainingSlots = MAX_IMAGES - selectedFiles.length;
    const acceptedFiles = files
      .filter((file) => file.type.startsWith("image/"))
      .slice(0, remainingSlots);

    selectedFiles.push(...acceptedFiles);

    if (acceptedFiles.length < files.length) {
      alert(
        `You can select a maximum of ${MAX_IMAGES} images. Only the first ${MAX_IMAGES} images have been selected.`,
      );
    }

    syncInputFiles();
    updatePreview();
  });
}

const updatePreview = () => {
  if (!imagesPreview) {
    return;
  }

  imagesPreview.innerHTML = "";

  if (selectedFiles.length === 0) {
    imagesPreview.innerHTML = '<p class="no-images">No images selected</p>';
    return;
  }

  selectedFiles.forEach((file, index) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const previewItem = document.createElement("div");
      previewItem.className = "image-preview-item";
      previewItem.innerHTML = `
        <img src="${e.target.result}" alt="Preview ${index + 1}">
        <button type="button" class="remove-image" data-index="${index}" aria-label="Remove image">
          <i class="bi bi-x"></i>
        </button>
        <span class="image-name">${file.name}</span>
      `;
      imagesPreview.appendChild(previewItem);
    };
    reader.readAsDataURL(file);
  });

  document.querySelectorAll(".remove-image").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      const index = Number.parseInt(btn.dataset.index, 10);
      selectedFiles.splice(index, 1);
      syncInputFiles();
      updatePreview();
    });
  });
};

// Initialize preview on page load
updatePreview();
