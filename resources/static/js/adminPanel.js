//This file is such long because it contais all the logic for the buttons (add and delete) of the classes
//users, schedule and charts in the admin panel


// Select all the items from the side menu and content sections
//For the navigation between sections
const navItems = document.querySelectorAll('.nav-item');
const contentSections = document.querySelectorAll('.content-section');

// Helper: get first cell text that is not the dynamic select-cell
function getFirstNonSelectCellText(tr) {
  const tds = tr.querySelectorAll('td');
  for (const td of tds) {
    if (!td.classList.contains('select-cell')) return td.textContent.trim();
  }
  return '';
}

//This is for show or hide the content of each section when we click on the nav items
// Add event listeners to each nav item
navItems.forEach(item => {
  item.addEventListener('click', (e) => {
    e.preventDefault();

    // Get the section name from data attribute
    const sectionName = item.getAttribute('data-section');

    // Remove the class 'active' from all nav items
    navItems.forEach(nav => nav.classList.remove('active'));

    // Add 'active' to the clicked nav item
    item.classList.add('active');

    // Remove the class 'active' from all content sections
    contentSections.forEach(section => section.classList.remove('active'));

    // Add 'active' to the corresponding content section
    const activeSection = document.getElementById(sectionName);
    if (activeSection) {
      activeSection.classList.add('active');
    }
  });
});

// ===== CLASS DELETE BUTTON =====
const classesTable = document.getElementById('classes-table');
if (classesTable) {
  classesTable.addEventListener('click', (event) => {
    const deleteButton = event.target.closest('.delete-class');
    if (!deleteButton) return;

    const row = deleteButton.closest('tr');
    if (row) row.remove();
  });
}

// ===== SCHEDULE EDIT MODE =====
const scheduleEditToggle = document.getElementById('toggle-edit-schedule');
const editScheduleForm = document.getElementById('edit-schedule-form');
const editScheduleClassInput = document.getElementById('edit-schedule-class');
const editScheduleTimeInput = document.getElementById('edit-schedule-time');
const saveScheduleChangesButton = document.getElementById('save-schedule-changes');
const schedulesList = document.querySelector('#schedule .schedules-list');

if (scheduleEditToggle && editScheduleForm && schedulesList) {
  let scheduleEditMode = false;
  let selectedScheduleEntry = null;

  const setScheduleFormDisabled = (disabled) => {
    editScheduleClassInput.disabled = disabled;
    editScheduleTimeInput.disabled = disabled;
    saveScheduleChangesButton.disabled = disabled;
    editScheduleForm.classList.toggle('is-disabled', disabled);
  };

  const buildScheduleEntry = (li) => {
    const timeText = li.querySelector('.schedule-time')
      ? li.querySelector('.schedule-time').textContent.trim()
      : li.textContent.trim();

    li.textContent = '';
    li.classList.add('schedule-entry');

    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.className = 'schedule-select';
    checkbox.setAttribute('aria-label', 'Select schedule');

    const timeSpan = document.createElement('span');
    timeSpan.className = 'schedule-time';
    timeSpan.textContent = timeText;

    li.appendChild(checkbox);
    li.appendChild(timeSpan);
  };

  const ensureScheduleSelectCells = () => {
    schedulesList.querySelectorAll('.schedule-list li').forEach(li => {
      const hasEntry = li.querySelector('.schedule-time');
      if (!hasEntry) {
        buildScheduleEntry(li);
      } else {
        li.classList.add('schedule-entry');
        const checkbox = li.querySelector('.schedule-select');
        if (!checkbox) {
          buildScheduleEntry(li);
        }
      }
    });
  };

  const clearScheduleSelection = () => {
    schedulesList.querySelectorAll('.schedule-select').forEach(input => {
      input.checked = false;
    });
    schedulesList.querySelectorAll('.schedule-entry').forEach(li => {
      li.classList.remove('selected');
    });

    selectedScheduleEntry = null;
    editScheduleClassInput.value = '';
    editScheduleTimeInput.value = '';
    setScheduleFormDisabled(true);
  };

  const setScheduleEditMode = (isActive) => {
    scheduleEditMode = isActive;
    if (scheduleEditMode) {
      ensureScheduleSelectCells();
      schedulesList.querySelectorAll('.schedule-list').forEach(list => {
        list.classList.add('is-selectable');
      });
      scheduleEditToggle.textContent = 'Cancel edit';
      setScheduleFormDisabled(true);
    } else {
      schedulesList.querySelectorAll('.schedule-list').forEach(list => {
        list.classList.remove('is-selectable');
      });
      scheduleEditToggle.textContent = 'Edit schedule';
      clearScheduleSelection();
    }
  };

  scheduleEditToggle.addEventListener('click', () => {
    setScheduleEditMode(!scheduleEditMode);
  });

  schedulesList.addEventListener('change', (event) => {
    if (!scheduleEditMode) return;

    const checkbox = event.target;
    if (!checkbox.classList.contains('schedule-select')) return;

    const li = checkbox.closest('li');
    if (!li) return;

    if (checkbox.checked) {
      schedulesList.querySelectorAll('.schedule-select').forEach(input => {
        if (input !== checkbox) input.checked = false;
      });

      schedulesList.querySelectorAll('.schedule-entry').forEach(entry => {
        entry.classList.toggle('selected', entry === li);
      });

      const scheduleItem = li.closest('.schedule-item');
      const classTitle = scheduleItem?.querySelector('h5');
      const timeSpan = li.querySelector('.schedule-time');

      editScheduleClassInput.value = classTitle?.textContent.trim() || '';
      editScheduleTimeInput.value = timeSpan?.textContent.trim() || '';
      selectedScheduleEntry = li;
      setScheduleFormDisabled(false);
    } else {
      li.classList.remove('selected');
      selectedScheduleEntry = null;
      editScheduleClassInput.value = '';
      editScheduleTimeInput.value = '';
      setScheduleFormDisabled(true);
    }
  });

  editScheduleForm.addEventListener('submit', (event) => {
    event.preventDefault();
    if (!selectedScheduleEntry) return;

    const updatedClassName = editScheduleClassInput.value.trim();
    const updatedSchedule = editScheduleTimeInput.value.trim();

    const scheduleItem = selectedScheduleEntry.closest('.schedule-item');
    const classTitle = scheduleItem?.querySelector('h5');
    const timeSpan = selectedScheduleEntry.querySelector('.schedule-time');

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

