document.addEventListener("DOMContentLoaded", function () {
  const ctx = document.getElementById("classDistributionChart");
  if (!ctx) return;

  const labels = Object.keys(classDistribution);
  const values = Object.values(classDistribution);

  if (labels.length === 0) {
    document.getElementById("noActivityMsg").style.display = "block";
    ctx.style.display = "none";
    return;
  }

  const colors = [
    "#fbcc23",
    "#0096be",
    "#64c832",
    "#ff6b6b",
    "#9b59b6",
    "#3498db",
    "#2ecc71",
    "#e67e22",
    "#e74c3c",
  ];

  new Chart(ctx, {
    type: "doughnut",
    data: {
      labels: labels,
      datasets: [
        {
          data: values,
          backgroundColor: colors.slice(0, labels.length),
          borderWidth: 2,
          borderColor: "#1a1a2e",
        },
      ],
    },
    options: {
      responsive: true,
      plugins: {
        legend: { position: "bottom", labels: { font: { size: 11 } } },
        tooltip: {
          callbacks: {
            label: function (context) {
              const total = context.dataset.data.reduce((a, b) => a + b, 0);
              const pct = Math.round((context.parsed / total) * 100);
              return ` ${context.label}: ${context.parsed} sessions (${pct}%)`;
            },
          },
        },
      },
    },
  });
});
