// Class.js - Comment editing functionality

document.addEventListener("DOMContentLoaded", function () {
  // Initialize edit buttons
  initializeEditButtons();
});

/**
 * Initialize edit buttons for all comments
 */
function initializeEditButtons() {
  const editButtons = document.querySelectorAll(".btn-edit-comment");
  editButtons.forEach((button) => {
    button.addEventListener("click", function (e) {
      e.preventDefault();
      const commentItem = this.closest(".comment-item");
      if (commentItem) {
        enterEditMode(commentItem);
      }
    });
  });
}

/**
 * Enter edit mode for a comment
 * @param {Element} commentItem - The comment item element
 */
function enterEditMode(commentItem) {
  // Hide the original content
  const header = commentItem.querySelector(".comment-header");
  const meta = commentItem.querySelector(".comment-meta");
  const text = commentItem.querySelector(".comment-text");

  if (!header || !text) return;

  // Extract current rating
  const ratingElement = commentItem.querySelector(".comment-rating");
  const currentRating = ratingElement ? countFilledStars(ratingElement) : 5;

  // Hide original content and action buttons
  meta.style.display = "none";
  text.style.display = "none";

  const actions = header.querySelector(".comment-actions");
  if (actions) {
    actions.style.display = "none";
  }

  // Create edit form
  const editForm = createEditForm(
    text.textContent.trim(),
    currentRating,
    commentItem,
  );

  // Insert edit form after avatar, before actions (inside header)
  header.appendChild(editForm);

  // Focus on textarea
  const textarea = editForm.querySelector(".edit-comment-textarea");
  textarea.focus();
}

/**
 * Count filled stars in a rating element
 * @param {Element} ratingElement - The rating element
 * @returns {number} - Number of filled stars
 */
function countFilledStars(ratingElement) {
  const icons = ratingElement.querySelectorAll("i");
  let count = 0;
  icons.forEach((icon) => {
    if (icon.classList.contains("bi-star-fill")) {
      count++;
    }
  });
  return count;
}

/**
 * Create the edit form element
 * @param {string} originalText - The original comment text
 * @param {number} currentRating - The current rating
 * @param {Element} commentItem - The comment item element
 * @returns {Element} - The edit form element
 */
function createEditForm(originalText, currentRating, commentItem) {
  const editContainer = document.createElement("div");
  editContainer.className = "edit-comment-form";
  editContainer.innerHTML = `
    <div class="edit-form-group">
      <label for="edit-rating-${Date.now()}">Rating:</label>
      <select class="form-control edit-comment-rating" id="edit-rating-${Date.now()}">
        <option value="5" ${currentRating === 5 ? "selected" : ""}>★★★★★ Excellent</option>
        <option value="4" ${currentRating === 4 ? "selected" : ""}>★★★★☆ Good</option>
        <option value="3" ${currentRating === 3 ? "selected" : ""}>★★★☆☆ Average</option>
        <option value="2" ${currentRating === 2 ? "selected" : ""}>★★☆☆☆ Fair</option>
        <option value="1" ${currentRating === 1 ? "selected" : ""}>★☆☆☆☆ Poor</option>
      </select>
    </div>

    <div class="edit-form-group">
      <label for="edit-text-${Date.now()}">Comment:</label>
      <textarea class="form-control edit-comment-textarea" id="edit-text-${Date.now()}" rows="4">${originalText}</textarea>
    </div>

    <div class="edit-form-actions">
      <button type="button" class="btn btn-sm btn-success btn-save-comment">
        <i class="bi bi-check"></i> Save
      </button>
      <button type="button" class="btn btn-sm btn-secondary btn-cancel-comment">
        <i class="bi bi-x"></i> Cancel
      </button>
    </div>
  `;

  // Add event listeners
  const saveBtn = editContainer.querySelector(".btn-save-comment");
  const cancelBtn = editContainer.querySelector(".btn-cancel-comment");

  saveBtn.addEventListener("click", function () {
    saveCommentChanges(commentItem, editContainer);
  });

  cancelBtn.addEventListener("click", function () {
    exitEditMode(commentItem, editContainer);
  });

  // Allow Enter key to save (with Ctrl)
  const textarea = editContainer.querySelector(".edit-comment-textarea");
  textarea.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
      exitEditMode(commentItem, editContainer);
    }
    if (e.ctrlKey && e.key === "Enter") {
      saveCommentChanges(commentItem, editContainer);
    }
  });

  return editContainer;
}

/**
 * Save comment changes
 * @param {Element} commentItem - The comment item element
 * @param {Element} editContainer - The edit form container
 */
function saveCommentChanges(commentItem, editContainer) {
  const newRating = editContainer.querySelector(".edit-comment-rating").value;
  const newText = editContainer
    .querySelector(".edit-comment-textarea")
    .value.trim();

  // Validate
  if (!newText || newText.length === 0) {
    alert("Please enter a comment");
    return;
  }

  if (!newRating) {
    alert("Please select a rating");
    return;
  }

  // Update the comment display
  updateCommentDisplay(commentItem, newRating, newText);

  // Remove edit form
  exitEditMode(commentItem, editContainer, true);

  // Here you would typically send the changes to the server
  console.log("Comment saved:", { rating: newRating, text: newText });
  // Example: sendCommentUpdate(commentId, newRating, newText);
}

/**
 * Update the comment display with new values
 * @param {Element} commentItem - The comment item element
 * @param {number} newRating - The new rating
 * @param {string} newText - The new comment text
 */
function updateCommentDisplay(commentItem, newRating, newText) {
  // Update text
  const textElement = commentItem.querySelector(".comment-text");
  if (textElement) {
    textElement.textContent = newText;
  }

  // Update rating
  const ratingElement = commentItem.querySelector(".comment-rating");
  if (ratingElement) {
    updateRatingDisplay(ratingElement, parseInt(newRating));
  }
}

/**
 * Update the visual rating display (stars)
 * @param {Element} ratingElement - The rating element
 * @param {number} rating - The rating value (1-5)
 */
function updateRatingDisplay(ratingElement, rating) {
  let starsHTML = "";
  for (let i = 1; i <= 5; i++) {
    if (i <= rating) {
      starsHTML += '<i class="bi bi-star-fill"></i>';
    } else {
      starsHTML += '<i class="bi bi-star"></i>';
    }
  }
  ratingElement.innerHTML = starsHTML;
}

/**
 * Exit edit mode and restore original content
 * @param {Element} commentItem - The comment item element
 * @param {Element} editContainer - The edit form container
 * @param {boolean} saved - Whether changes were saved
 */
function exitEditMode(commentItem, editContainer, saved = false) {
  // Show original content
  const meta = commentItem.querySelector(".comment-meta");
  const text = commentItem.querySelector(".comment-text");
  const header = commentItem.querySelector(".comment-header");
  const actions = header ? header.querySelector(".comment-actions") : null;

  if (meta) meta.style.display = "block";
  if (text) text.style.display = "block";
  if (actions) actions.style.display = "flex";

  // Remove edit form
  editContainer.remove();

  // Show success message if saved
  if (saved) {
    showNotification("Comment updated successfully!", "success");
  }
}

/**
 * Show a notification message
 * @param {string} message - The message to display
 * @param {string} type - The notification type (success, info, warning, error)
 */
function showNotification(message, type = "info") {
  // Create notification element
  const notification = document.createElement("div");
  notification.className = `alert alert-${type === "success" ? "success" : type === "error" ? "danger" : type} alert-notification`;
  notification.setAttribute("role", "alert");
  notification.textContent = message;
  notification.style.cssText = `
    position: fixed;
    top: 100px;
    right: 20px;
    z-index: 9999;
    min-width: 300px;
    animation: slideIn 0.3s ease-in-out;
  `;

  document.body.appendChild(notification);

  // Remove after 3 seconds
  setTimeout(() => {
    notification.style.animation = "slideOut 0.3s ease-in-out";
    setTimeout(() => notification.remove(), 300);
  }, 3000);
}

// Add CSS animations if not already present
if (!document.querySelector("#class-js-styles")) {
  const style = document.createElement("style");
  style.id = "class-js-styles";
  style.textContent = `
    @keyframes slideIn {
      from {
        transform: translateX(400px);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    @keyframes slideOut {
      from {
        transform: translateX(0);
        opacity: 1;
      }
      to {
        transform: translateX(400px);
        opacity: 0;
      }
    }

    .alert-notification {
      display: flex;
      align-items: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
  `;
  document.head.appendChild(style);
}
