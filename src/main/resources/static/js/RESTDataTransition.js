<!--Функция отправки данных REST-контроллеру-->
function RESTDataTransition(userData, method) {
    // Преобразуем объект в JSON
    let jsonData;
    let url;

    if (method === 'DELETE') {
        jsonData = JSON.stringify({userData});
        url = '/api/users/' + userData;
    } else if (method === 'PUT' || method === 'POST') {
        jsonData = JSON.stringify(userData);
        url = 'http://localhost:8080/api/users';
    }

    console.log(jsonData);

    // Отправляем данные на сервер
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: jsonData,
    }).then(response => {
        console.log('Fetch Response:', response);
        if (response.ok) {
            console.log('Data processed successfully!');
            location.reload();
        } else {
            console.error('Failed to processed data');
            return response.text().then(text => { throw new Error(text) });
        }
    })
        .then(data => {
            if(method = 'POST') {
                console.log('Success:', data);
                window.location.href = '/admin';
            }
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}