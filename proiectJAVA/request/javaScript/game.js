if (localStorage.getItem('email') !== null && localStorage.getItem('hashcode') !== null) {
    document.getElementById('email').setAttribute('value', localStorage.getItem('email'));
    document.getElementById('hashcode').setAttribute('value', localStorage.getItem('hashcode'));
}


function startGame() {
    var email = document.getElementById("email").value;
    var hashcode = document.getElementById("hashcode").value;

    if (email != "" && hashcode != "") {
        var url = 'https://localhost:8443/api/v1/quiz/game';
        var xhr = new XMLHttpRequest();
        var data = JSON.stringify({ "person": { "email": email, "hashcode": hashcode }, "response": "" });

        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {

                if (xhr.status == 404) {
                    alert(xhr.responseText);
                } else if (xhr.status == 200) {


                    localStorage.setItem('email', email);
                    localStorage.setItem('hashcode', hashcode);
                    document.querySelector(".form-container").style.display = 'none';
                    document.querySelector(".quizz-container").style.display = 'block';
                    var response = JSON.parse(xhr.responseText);
                    updateQuestion(response);

                }
                else {
                    alert("[Eroare]O sa vedem mai tarziuuuu.");
                }
            }

        };

        xhr.open('POST', url, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(data);
    }
    else {
        alert("Trebuie sa completati ambele campuri.");
    }

}



function sendAnswerToServer() {
    var answers = getSelectedAnswers();



    if (answers.length != 0) {
        var currentAnswer = null;
        var question = document.getElementById('question-id').innerHTML;
        currentAnswer = question + "," + answers.toString();
        //alert(currentAnswer);

        var url = 'https://localhost:8443/api/v1/quiz/game';
        var xhr = new XMLHttpRequest();
        var data = JSON.stringify({
            "person": { "email": localStorage.getItem('email'), "hashcode": localStorage.getItem('hashcode') },
            "response": currentAnswer
        });

        //alert(data);

        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                var response = JSON.parse(xhr.responseText);
                if (response.hasOwnProperty("message")) {
                    alert(response["message"]);
                } else if (response.hasOwnProperty("domeniu")) {
                    updateQuestion(response);
                } else {
                    localStorage.setItem('score', xhr.responseText);
                    window.location.replace("../html/score.html");
                }
            }
        };


        xhr.open('POST', url, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(data);
    }

    else {
        alert("Trebuie sa selectati cel putin un raspuns!");
    }
}




function getSelectedAnswers() {
    var checkboxes = document.getElementsByName('checkbox');
    var answers = [];
    for (var i = 0; i < checkboxes.length; i++) {
        if (checkboxes[i].checked) {
            answers.push(checkboxes[i].value);
        }
    }
    return answers;
}



function updateQuestion(response) {
    document.getElementById("question-number").innerText = response["numar_intrebare"];
    document.getElementById("domain").innerText = "Domeniu: " + response["domeniu"];
    document.getElementById("question-id").innerText = response["id_intrebare"];
    document.getElementById("question").innerText = response["intrebare"];

    var answerId = "id_rasp_";
    var checkboxes = document.getElementsByName('checkbox');

    for (var i = 0; i < checkboxes.length; i++) {
        answerId = answerId + (i + 1);
        checkboxes[i].value = response[answerId];
        checkboxes[i].id = answerId;
        checkboxes[i].checked = false;
        answerId = answerId.substring(0, answerId.length - 1);
    }

    var answer = "raspuns_";
    var labels = document.getElementsByTagName('label');

    for (var i = 0; i < labels.length - 1; i++) {
        answer = answer + i;
        answerId = answerId + i;
        labels[i + 1].innerText = response[answer];
        labels[i + 1].htmlFor = response[answerId];
        answer = answer.substring(0, answer.length - 1);
        answerId = answerId.substring(0, answerId.length - 1);
    }
}


