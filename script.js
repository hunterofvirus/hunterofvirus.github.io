const form = document.getElementById("form");
const results = document.getElementById("results");
const totalElement = document.getElementById("total");

const initialMachines = [
    "73080-GAI Cossi Stanislas",
    "73082-VODOUNOU Lucrèce",
    "73169-BOUBOUYABOU Mic",
    "73179-DEDEGBE Boris",
    "77184-DALMEIDA C",
];

let total = 0;

initialMachines.forEach((machine) => {
    addRow(machine, 0, true);
});

form.addEventListener("submit", (event) => {
    event.preventDefault();

    const machine = event.target.elements.machine.value;
    const variable = parseFloat(event.target.elements.variable.value);

    addRow(machine, variable);

    total += variable;
    totalElement.textContent = total.toFixed(2);

    form.reset();
});

function addRow(machine, variable, isInitialRow = false) {
    const row = document.createElement("tr");
    const netNumber = results.querySelectorAll("tr").length + 1;
    const result = variable / 1.1;
    row.innerHTML = `
        <td>${machine}</td>
        <td>${isInitialRow ? `<input type="number" step="any" class="variable" value="${variable.toFixed(2)}">` : variable.toFixed(2)}</td>
        <td>NET${netNumber}</td>
        <td>${result ? result.toFixed(2) : ""}</td>
        <td>${isInitialRow ? "" : '<button class="delete">Supprimer</button>'}</td>
    `;
    results.appendChild(row);

    if (isInitialRow) {
        const input = row.querySelector(".variable");
        input.addEventListener("input", () => {
            const newValue = parseFloat(input.value) || 0;
            const oldValue = parseFloat(input.defaultValue) || 0;
            row.children[3].textContent = (newValue / 1.1).toFixed(2);
            total += newValue - oldValue;
            totalElement.textContent = total.toFixed(2);
            input.defaultValue = newValue.toFixed(2);
        });
    } else {
        row.querySelector(".delete").addEventListener("click", () => {
            total -= variable;
            totalElement.textContent = total.toFixed(2);
            row.remove();
            updateNetNumbers();
        });
    }
}

function updateNetNumbers() {
    const rows = results.querySelectorAll("tr");
    rows.forEach((row, index) => {
        row.children[2].textContent = `NET${index + 1}`;
    });
}
