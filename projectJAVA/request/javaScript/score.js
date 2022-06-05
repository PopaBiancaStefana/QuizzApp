var retrievedObject = localStorage.getItem('score');

function showScore() {

    var container = document.getElementById("score-container");
    var response = JSON.parse(retrievedObject);

    var score = document.createElement("h3");
    score.innerText = "Scor total: " + response["total-score"] + " puncte";

    container.appendChild(score);

    var ol = document.createElement('ol');
    var question = "intrebare_1";
    var index = 1;

    while (response.hasOwnProperty(question)) {
        var li = document.createElement('li');

        li.appendChild(createUlForAnswer(response, question, index));
        ol.appendChild(li);

        question = question.substring(0, question.length - 1);
        ++index;
        question = question + index;
    }
    container.appendChild(ol);

}

function createUlForAnswer(response, question, index) {
    var ul = document.createElement('ul');

    var li = document.createElement('li');
    li.style.fontWeight = 'bold'
    li.innerHTML = response[question]["intrebare"];
    ul.appendChild(li);

    var li = document.createElement('li');
    var text = document.createTextNode("Raspunsuri corecte: ");
    li.appendChild(text);
    li.appendChild(getCorrectAnswers(response, question));
    ul.appendChild(li);



    var li = document.createElement('li');
    var text = document.createTextNode("Raspunsuri alese: ");
    li.appendChild(text);
    li.appendChild(getUserAnswers(response, question));
    ul.appendChild(li);


    var li = document.createElement('li');
    li.innerHTML = "Scorul intrebarii : " + response[question]["punctaj"];
    ul.appendChild(li);

    return ul;
}


function getCorrectAnswers(response, question) {
    var ul = document.createElement('ul');

    var number = 1;
    var answer = "raspuns_ok_1";

    while (response[question][answer] != undefined) {
        var li = document.createElement('li');
        var text = document.createTextNode(response[question][answer]);
        li.appendChild(text);
        ul.appendChild(li);

        answer = answer.substring(0, answer.length - 1);
        ++number;
        answer = answer + number;
    }
    return ul;
}

function getUserAnswers(response, question) {
    var ul = document.createElement('ul');

    var number = 1;
    var answer = "raspuns_1";

    while (response[question][answer] != undefined) {
        var li = document.createElement('li');
        var text = document.createTextNode(response[question][answer]);
        li.appendChild(text);
        ul.appendChild(li);

        answer = answer.substring(0, answer.length - 1);
        ++number;
        answer = answer + number;
    }
    return ul;
}

function CheckKey(key, object) {
    for (var name in object) {

        if (typeof object[name] == 'object') {
            var newObj = object[name];
            return CheckKey(key, newObj)
        }
        else if (name == key) {
            return true;
        }
    }
    return false;
}

function displayAllScores() {
    var url = 'https://localhost:8443/api/v1/quiz/scores';
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            var response = JSON.parse(xhr.responseText);
            if (xhr.status == 200) {
                var player = "player_1";
                var index = 1;

                var usersScores = document.getElementById("users-scores");
                var title = document.createElement("h2");
                title.innerText = "Punctajul tuturor jucatorilor";
                title.style.margin = '5% 33%'
                usersScores.appendChild(title);

                var myTableDiv = document.getElementById("users-scores");

                var table = document.createElement('TABLE');
                table.border = '1';
                table.style.margin = '5% 10%';

                var tableBody = document.createElement('TBODY');
                table.appendChild(tableBody);

                var tr = document.createElement('TR');
                tableBody.appendChild(tr);
                var th1 = document.createElement('TH');
                th1.appendChild(document.createTextNode("Loc"));
                tr.appendChild(th1);

                var th2 = document.createElement('TH');
                th2.appendChild(document.createTextNode("Email"));
                tr.appendChild(th2);

                var th3 = document.createElement('TH');
                th3.appendChild(document.createTextNode("Punctaj"));
                tr.appendChild(th3);
                while (response.hasOwnProperty(player)) {
                    var tr = document.createElement('TR');
                    tableBody.appendChild(tr);
                    var td1 = document.createElement('TD');
                    td1.appendChild(document.createTextNode(index));
                    tr.appendChild(td1);

                    var td2 = document.createElement('TD');
                    td2.appendChild(document.createTextNode(response[player]["email"]));
                    tr.appendChild(td2);

                    var td3 = document.createElement('TD');
                    td3.appendChild(document.createTextNode(response[player]["punctaj"]));
                    tr.appendChild(td3);


                    player = player.substring(0, player.length - 1);
                    ++index;
                    player = player + index;
                }
                table.style.width = '80%'
                myTableDiv.appendChild(table);
            } else {
                alert("[Eroare]O sa vedem mai tarziu.");
            }
        }
    };

    xhr.open('GET', url, true);
    xhr.send();
}

function goToScores() {
    document.querySelector(".container").style.display = 'none';
    document.querySelector(".container2").style.display = 'block';
}

function backToResults() {
    document.querySelector(".container2").style.display = 'none';
    document.querySelector(".container").style.display = 'block';
}

showScore();
displayAllScores();