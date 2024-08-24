document.querySelector('.btn-add-user').addEventListener('click', function() {
    // Собираем данные из формы
    const firstName = document.getElementById('firstName').value;
    const lastName = document.getElementById('lastName').value;
    const age = document.getElementById('age').value;
    const username = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const rolesSelect = document.getElementById('roles');
    const roles = Array.from(rolesSelect.selectedOptions).map(option => option.value);
    const method = 'POST';

    // Формируем объект для отправки
    const userData = {
        firstName: firstName,
        lastName: lastName,
        age: age,
        username: username,
        password: password,
        roles: Array.from(document.getElementById('roles').selectedOptions).map(option => {
            return {
                id: option.value, // здесь должен быть ID роли
                name: option.text, // здесь может быть имя роли, если необходимо
                authority: option.text // здесь также можно использовать значение option.value или другие данные роли
            }
        })
    };

    console.log(userData);

    RESTDataTransition(userData, method);

});