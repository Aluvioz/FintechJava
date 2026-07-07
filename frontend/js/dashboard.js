const token = localStorage.getItem("token");

if (!token) {
    window.location.href = "index.html";
}

const balanceValue = document.getElementById("balance-value");
const userName = document.getElementById("user-name");
const accountNumber = document.getElementById("account-number");
const transferForm = document.getElementById("transfer-form");
const errorBox = document.getElementById("error-box");
const successBox = document.getElementById("success-box");
const transactionsList = document.getElementById("transactions-list");
const logoutBtn = document.getElementById("logout-btn");

let myAccount = null;

async function loadAccount() {
    try {
        myAccount = await apiRequest("/accounts/me");
        userName.textContent = myAccount.userName;
        accountNumber.textContent = `Conta nº ${myAccount.accountNumber}`;
        balanceValue.textContent = formatCurrency(myAccount.balance);
    } catch (error) {
        showError(error.message);
    }
}

async function loadTransactions() {
    try {
        const transactions = await apiRequest(`/accounts/${myAccount.accountId}/transactions`);
        renderTransactions(transactions);
    } catch (error) {
        console.error("Não foi possível carregar o extrato:", error.message);
    }
}

function renderTransactions(transactions) {
    transactionsList.innerHTML = "";

    if (transactions.length === 0) {
        transactionsList.innerHTML = '<p style="color:#64748b; font-size:13px;">Nenhuma transação ainda.</p>';
        return;
    }

    transactions.forEach((tx) => {
        const isIncoming = tx.type === "DEPOSIT" || tx.type === "TRANSFER_IN";
        const div = document.createElement("div");
        div.className = `transaction-item ${isIncoming ? "in" : "out"}`;
        div.innerHTML = `
            <span>${tx.description}</span>
            <span>${isIncoming ? "+" : "-"} ${formatCurrency(tx.amount)}</span>
        `;
        transactionsList.appendChild(div);
    });
}

transferForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    hideMessages();

    const destinationAccountNumber = document.getElementById("destination").value;
    const amount = parseFloat(document.getElementById("amount").value);

    try {
        await apiRequest("/accounts/transfer-by-number", "POST", {
            originAccountId: myAccount.accountId,
            destinationAccountNumber,
            amount
        });

        showSuccess("Transferência realizada com sucesso!");
        transferForm.reset();
        await loadAccount();
        await loadTransactions();

    } catch (error) {
        showError(error.message);
    }
});

logoutBtn.addEventListener("click", () => {
    localStorage.removeItem("token");
    window.location.href = "index.html";
});

function formatCurrency(value) {
    return value.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}

function showError(message) {
    errorBox.textContent = message;
    errorBox.style.display = "block";
}

function showSuccess(message) {
    successBox.textContent = message;
    successBox.style.display = "block";
}

function hideMessages() {
    errorBox.style.display = "none";
    successBox.style.display = "none";
}

loadAccount().then(() => loadTransactions());