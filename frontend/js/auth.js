let isRegisterMode = false;

const form = document.getElementById("auth-form");
const formTitle = document.getElementById("form-title");
const nameField = document.getElementById("name-field");
const submitBtn = document.getElementById("submit-btn");
const toggleLink = document.getElementById("toggle-link");
const errorBox = document.getElementById("error-box");

toggleLink.addEventListener("click", () => {
    isRegisterMode = !isRegisterMode;

    if (isRegisterMode) {
        formTitle.textContent = "Criar sua conta";
        nameField.style.display = "block";
        submitBtn.textContent = "Cadastrar";
        toggleLink.innerHTML = 'Já tem conta? <span>Entrar</span>';
    } else {
        formTitle.textContent = "Entrar na sua conta";
        nameField.style.display = "none";
        submitBtn.textContent = "Entrar";
        toggleLink.innerHTML = 'Não tem conta? <span>Cadastre-se</span>';
    }

    hideError();
});

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    hideError();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        let response;

        if (isRegisterMode) {
            const name = document.getElementById("name").value;
            response = await apiRequest("/auth/register", "POST", { name, email, password }, false);
        } else {
            response = await apiRequest("/auth/login", "POST", { email, password }, false);
        }

        localStorage.setItem("token", response.token);
        window.location.href = "dashboard.html";

    } catch (error) {
        showError(error.message);
    }
});

function showError(message) {
    errorBox.textContent = message;
    errorBox.style.display = "block";
}

function hideError() {
    errorBox.style.display = "none";
}