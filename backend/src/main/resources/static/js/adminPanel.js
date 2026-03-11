//This file is such long because it contais all the logic for the buttons (add and delete) of the classes
//users, schedule and charts in the admin panel

// Select all the items from the side menu and content sections
//For the navigation between sections
const navItems = document.querySelectorAll(".sidebar-nav .nav-item");
const contentSections = document.querySelectorAll(".content-section");

// Helper: get first cell text that is not the dynamic select-cell
function getFirstNonSelectCellText(tr) {
  const tds = tr.querySelectorAll("td");
  for (const td of tds) {
    if (!td.classList.contains("select-cell")) return td.textContent.trim();
  }
  return "";
}

//This is for show or hide the content of each section when we click on the nav items
// Add event listeners to each nav item
navItems.forEach((item) => {
  item.addEventListener("click", (e) => {
    e.preventDefault();

    // Get the section name from data attribute
    const sectionName = item.getAttribute("data-section");

    // Remove the class 'active' from all nav items
    navItems.forEach((nav) => nav.classList.remove("active"));

    // Add 'active' to the clicked nav item
    item.classList.add("active");

    // Remove the class 'active' from all content sections
    contentSections.forEach((section) => section.classList.remove("active"));

    // Add 'active' to the corresponding content section
    const activeSection = document.getElementById(sectionName);
    if (activeSection) {
      activeSection.classList.add("active");
    }
  });
});

// ===== CLASS DELETE BUTTON =====
const classesTable = document.getElementById("classes-table");
if (classesTable) {
  classesTable.addEventListener("click", (event) => {
    const deleteButton = event.target.closest(".delete-class");
    if (!deleteButton) return;

    const row = deleteButton.closest("tr");
    if (row) row.remove();
  });
}

// ===== SCHEDULE EDIT MODE =====
const scheduleEditToggle = document.getElementById("toggle-edit-schedule");
const editScheduleForm = document.getElementById("edit-schedule-form");
const editScheduleClassInput = document.getElementById("edit-schedule-class");
const editScheduleTimeInput = document.getElementById("edit-schedule-time");
const saveScheduleChangesButton = document.getElementById(
  "save-schedule-changes",
);
const schedulesList = document.querySelector("#schedule .schedules-list");

if (scheduleEditToggle && editScheduleForm && schedulesList) {
  let scheduleEditMode = false;
  let selectedScheduleEntry = null;

  const setScheduleFormDisabled = (disabled) => {
    editScheduleClassInput.disabled = disabled;
    editScheduleTimeInput.disabled = disabled;
    saveScheduleChangesButton.disabled = disabled;
    editScheduleForm.classList.toggle("is-disabled", disabled);
  };

  const buildScheduleEntry = (li) => {
    const timeText = li.querySelector(".schedule-time")
      ? li.querySelector(".schedule-time").textContent.trim()
      : li.textContent.trim();

    li.textContent = "";
    li.classList.add("schedule-entry");

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.className = "schedule-select";
    checkbox.setAttribute("aria-label", "Select schedule");

    const timeSpan = document.createElement("span");
    timeSpan.className = "schedule-time";
    timeSpan.textContent = timeText;

    li.appendChild(checkbox);
    li.appendChild(timeSpan);
  };

  const ensureScheduleSelectCells = () => {
    schedulesList.querySelectorAll(".schedule-list li").forEach((li) => {
      const hasEntry = li.querySelector(".schedule-time");
      if (!hasEntry) {
        buildScheduleEntry(li);
      } else {
        li.classList.add("schedule-entry");
        const checkbox = li.querySelector(".schedule-select");
        if (!checkbox) {
          buildScheduleEntry(li);
        }
      }
    });
  };

  const clearScheduleSelection = () => {
    schedulesList.querySelectorAll(".schedule-select").forEach((input) => {
      input.checked = false;
    });
    schedulesList.querySelectorAll(".schedule-entry").forEach((li) => {
      li.classList.remove("selected");
    });

    selectedScheduleEntry = null;
    editScheduleClassInput.value = "";
    editScheduleTimeInput.value = "";
    setScheduleFormDisabled(true);
  };

  const setScheduleEditMode = (isActive) => {
    scheduleEditMode = isActive;
    if (scheduleEditMode) {
      ensureScheduleSelectCells();
      schedulesList.querySelectorAll(".schedule-list").forEach((list) => {
        list.classList.add("is-selectable");
      });
      scheduleEditToggle.textContent = "Cancel edit";
      setScheduleFormDisabled(true);
    } else {
      schedulesList.querySelectorAll(".schedule-list").forEach((list) => {
        list.classList.remove("is-selectable");
      });
      scheduleEditToggle.textContent = "Edit schedule";
      clearScheduleSelection();
    }
  };

  scheduleEditToggle.addEventListener("click", () => {
    setScheduleEditMode(!scheduleEditMode);
  });

  schedulesList.addEventListener("change", (event) => {
    if (!scheduleEditMode) return;

    const checkbox = event.target;
    if (!checkbox.classList.contains("schedule-select")) return;

    const li = checkbox.closest("li");
    if (!li) return;

    if (checkbox.checked) {
      schedulesList.querySelectorAll(".schedule-select").forEach((input) => {
        if (input !== checkbox) input.checked = false;
      });

      schedulesList.querySelectorAll(".schedule-entry").forEach((entry) => {
        entry.classList.toggle("selected", entry === li);
      });

      const scheduleItem = li.closest(".schedule-item");
      const classTitle = scheduleItem?.querySelector("h5");
      const timeSpan = li.querySelector(".schedule-time");

      editScheduleClassInput.value = classTitle?.textContent.trim() || "";
      editScheduleTimeInput.value = timeSpan?.textContent.trim() || "";
      selectedScheduleEntry = li;
      setScheduleFormDisabled(false);
    } else {
      li.classList.remove("selected");
      selectedScheduleEntry = null;
      editScheduleClassInput.value = "";
      editScheduleTimeInput.value = "";
      setScheduleFormDisabled(true);
    }
  });

  editScheduleForm.addEventListener("submit", (event) => {
    event.preventDefault();
    if (!selectedScheduleEntry) return;

    const updatedClassName = editScheduleClassInput.value.trim();
    const updatedSchedule = editScheduleTimeInput.value.trim();

    const scheduleItem = selectedScheduleEntry.closest(".schedule-item");
    const classTitle = scheduleItem?.querySelector("h5");
    const timeSpan = selectedScheduleEntry.querySelector(".schedule-time");

    if (classTitle && updatedClassName) {
      classTitle.textContent = updatedClassName;
    }
    if (timeSpan) {
      timeSpan.textContent = updatedSchedule;
    }

    clearScheduleSelection();
  });

  const scheduleObserver = new MutationObserver(() => {
    if (scheduleEditMode) {
      ensureScheduleSelectCells();
    }
  });

  scheduleObserver.observe(schedulesList, { childList: true, subtree: true });
}

// ===== CHARTS (original code unchanged) =====
// classes is available from Mustache if you embed it, or just iterate canvas elements
document.addEventListener("DOMContentLoaded", function () {
  const days = [];
  for (let i = 6; i >= 0; i--) {
    const d = new Date();
    d.setDate(d.getDate() - i);
    days.push(
      d.toLocaleDateString("en-US", {
        weekday: "short",
        month: "short",
        day: "numeric",
      }),
    );
  }

  const colors = [
    ["rgba(251,204,35,0.8)", "#fbcc23"],
    ["rgba(0,150,190,0.8)", "#0096be"],
    ["rgba(100,200,50,0.8)", "#64c832"],
    ["rgba(255,107,107,0.8)", "#ff6b6b"],
    ["rgba(155,89,182,0.8)", "#9b59b6"],
    ["rgba(52,152,219,0.8)", "#3498db"],
    ["rgba(46,204,113,0.8)", "#2ecc71"],
  ];

  function createClassChart(canvasId, label, data, color, borderColor) {
    const ctx = document.getElementById(canvasId);
    if (ctx) {
      new Chart(ctx, {
        type: "bar",
        data: {
          labels: days,
          datasets: [
            {
              label,
              data,
              backgroundColor: color,
              borderColor,
              borderWidth: 2,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: { legend: { display: true } },
          scales: { y: { beginAtZero: true, ticks: { stepSize: 5 } } },
        },
      });
    }
  }

  // Aggregate
  const classIds = Object.keys(chartDataFromServer);
  const aggregateData = days.map((_, idx) =>
    classIds.reduce((sum, id) => sum + (chartDataFromServer[id][idx] || 0), 0),
  );

  const aggregateCtx = document.getElementById("aggregateChart");
  if (aggregateCtx) {
    new Chart(aggregateCtx, {
      type: "line",
      data: {
        labels: days,
        datasets: [
          {
            label: "Total Attendance",
            data: aggregateData,
            backgroundColor: "rgba(251, 204, 35, 0.2)",
            borderColor: "#fbcc23",
            borderWidth: 3,
            fill: true,
            tension: 0.4,
            pointBackgroundColor: "#fbcc23",
            pointBorderColor: "#fff",
            pointBorderWidth: 2,
            pointRadius: 5,
          },
        ],
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: true, position: "top" } },
        scales: { y: { beginAtZero: true, ticks: { stepSize: 10 } } },
      },
    });
  }

  // Per-class — match canvas id to JSON key
  document.querySelectorAll('[id^="chartClass"]').forEach((canvas, i) => {
    const id = canvas.id.replace("chartClass", "");
    const data = chartDataFromServer[id] || [0, 0, 0, 0, 0, 0, 0];
    const label = canvas
      .closest(".chart-container")
      .querySelector("h4").textContent;
    const color = colors[i % colors.length];
    createClassChart(canvas.id, label, data, color[0], color[1]);
  });
});
