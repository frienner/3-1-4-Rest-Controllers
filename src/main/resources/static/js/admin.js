<!--Функция отображения модального окна редактирования пользователей-->
function showModal(data, modalId) {

    let method;

    if (modalId === 'editModal') {
        method = 'Edit';
    } else if (modalId === 'deleteModal') {
        method = 'Delete';
    }

    const idInput = document.getElementById('userId' + method);
    const firstNameInput = document.getElementById('firstName' + method);
    const lastNameInput = document.getElementById('lastName' + method);
    const ageInput = document.getElementById('age' + method);
    const emailInput = document.getElementById('email' + method);
    const passwordInput = document.getElementById('password' + method);
    const rolesInput = document.getElementById('roles' + method);

    console.log(firstNameInput, lastNameInput, ageInput, emailInput, passwordInput)

    idInput.value = data['Id'] || '';
    firstNameInput.value = data['First name'] || '';
    lastNameInput.value = data['Last name'] || '';
    ageInput.value = data['Age'] || '';
    emailInput.value = data['Email'] || '';
    passwordInput.value = data['Password'] || '';

    const selectedRoles = data['Roles'];

    for (let i = 0; i < rolesInput.options.length; i++) {
        rolesInput.options[i].selected = false;
    }

    for (let i = 0; i < rolesInput.options.length; i++) {
        const option = rolesInput.options[i];

        if (selectedRoles.includes(option.text)) {
            option.selected = true;
        }
    }

    // Дополнительная логика для отображения модального окна
    const modal = document.getElementById(modalId);
    modal.style.display = "block";
}

<!--Функция получения данных из строки и передачи их в модальное окно-->
function modalFill(button, modalId) {
    const row = button.parentElement.parentElement;
    const editData = {
        'Id': row.cells[0].innerText,
        'First name': row.cells[1].innerText,
        'Last name': row.cells[2].innerText,
        'Age': row.cells[3].innerText,
        'Email': row.cells[4].innerText,
        'Roles': row.cells[5].innerText
    };
    showModal(editData, modalId);
}

// Закрытие модального окна, если нажать вне него
window.onclick = function (event) {
    const modal = document.getElementById('editModal');
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

window.onclick = function(event) {
    const modal = document.getElementById('deleteModal');
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

<!--Функция, получающая данные из модального окна редактирования пользователя и передающая их функции для отправки данных-->
function saveChanges(button) {

    event.preventDefault();

    let method = document.getElementById('editRequestMethod').value;

    let idValue = document.getElementById('userIdEdit').value;

    let userData = {
        firstName: document.getElementById('firstNameEdit').value,
        lastName: document.getElementById('lastNameEdit').value,
        age: document.getElementById('ageEdit').value,
        username: document.getElementById('emailEdit').value,
        password: document.getElementById('passwordEdit').value,
        roles: Array.from(document.getElementById('rolesEdit').selectedOptions).map(option => {
            return {
                id: option.value, // здесь должен быть ID роли
                name: option.text, // здесь может быть имя роли, если необходимо
                authority: option.text // здесь также можно использовать значение option.value или другие данные роли
            }
        })

    };

    if (idValue) {
        userData.id = idValue;
    }

    // Вызов функции отправки данных
    RESTDataTransition(userData, method);

    const editModal = document.getElementById('editModal');
    const modalInstance = bootstrap.Modal.getInstance(editModal);
    modalInstance.hide();
}

function deleteUser(button) {
    let method = document.getElementById('deleteRequestMethod').value;

    let username = document.getElementById('emailDelete').value;

    RESTDataTransition(username, method);

    const deleteModal = document.getElementById('deleteModal');
    const modalInstance = bootstrap.Modal.getInstance(deleteModal);
    modalInstance.hide();
}

// Функция для подтверждения удаления
document.getElementById('submitDeleteButton').addEventListener('click', function() {
    const idInput = document.getElementById('userIdDelete').value;


});

// Закрытие модального окна, если нажать вне него
window.onclick = function(event) {
    const modal = document.getElementById('deleteModal');
    if (event.target === modal) {
        modal.style.display = "none";
    }
}