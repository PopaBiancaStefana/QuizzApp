function validateEmail() {
    var email = document.getElementById("email").value;

    if (/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
        sendEmailToServer();
    }
    else {
        alert("Te rugam sa introduci un email valid.");
    }
}

function sendEmailToServer() {
    var email = document.getElementById("email").value;

    if (email != "") {
        var url = 'https://localhost:8443/api/v1/quiz';
        var xhr = new XMLHttpRequest();
        var data = JSON.stringify({ "email": email });

        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {

                if (xhr.status == 201) {
                    var response = JSON.parse(xhr.responseText);
                    var hash = response["hashcode"];
                    localStorage.setItem('email', email);
                    localStorage.setItem('hashcode', hash);
                    alert("Hahcode-ul tau este " + hash + " \nPoti incepe testul dupa ce te loghezi cu datele primite.");
                    window.location.replace("../html/game.html");
                }
                else if (xhr.status == 409) {
                    alert("[Eroare]Deja exista un utilizator cu acest e-mail.")
                }
                else {
                    alert("[Eroare]Server in lucru...");
                }
            }
        };

        xhr.open('POST', url, true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(data);
    } else {
        alert("Trebuie sa introduceti un email.");
    }
}



function statistics() {
    scoreTable();
    peopleGraph();
    projectBuddiesSVG();
    getTeams();
}

function scoreTable() {
    document.querySelector(".form-container").style.display = 'none';
    document.querySelector(".container").style.display = 'block';
    var url = 'https://localhost:8443/api/v1/quiz/scores';
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            var response = JSON.parse(xhr.responseText);
            if (xhr.status == 200) {
                var player = "player_1";
                var index = 1;

                var usersScores = document.getElementById("stats-container");
                var title = document.createElement("h2");
                title.innerText = "Topul jucatorilor";
                title.style.margin = '80px 380px'
                usersScores.appendChild(title);

                var myTableDiv = document.getElementById("stats-container");

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
                alert("[Eroare]Oops...A aparut o eroare, incearca sa faci refresh.");
            }
        }
    };

    xhr.open('GET', url, true);
    xhr.send();
}


function peopleGraph() {
    document.querySelector(".form-container").style.display = 'none';
    document.querySelector(".container").style.display = 'block';
    var url = 'https://localhost:8443/api/v1/quiz/test';
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            //var response = JSON.parse(xhr.responseText);
            if (xhr.status == 200) {

                var graph = document.getElementById("graph");
                while (graph.firstChild) {
                    graph.removeChild(graph.firstChild);
                }

                var title = document.createElement("h2");
                title.innerText = "Observa cu cine ai lucruri in comun!*";
                title.style.margin = '0% 25%'
                graph.appendChild(title);

                var title = document.createElement("h2");
                title.innerText = "Graful jucatorilor";
                title.style.margin = '2% 37%'
                graph.appendChild(title);

                graph.innerHTML = graph.innerHTML + xhr.responseText;

                var smallText = document.createElement("p");
                smallText.innerText = "*Legaturile se realizeaza intre persoanele ce au macar 5 domenii in comun.";
                graph.appendChild(smallText);

            } else {
                alert("[Eroare]Server in lucru...");
            }
        }
    };

    xhr.open('GET', url, true);
    xhr.send();
}
function getTeams() {
    document.querySelector(".form-container").style.display = 'none';
    document.querySelector(".container").style.display = 'block';
    var url = 'https://localhost:8443/api/v1/quiz/graph';
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == 200) {
                var myTableDiv = document.getElementById("teams");

                var table = document.createElement('TABLE');
                table.border = '1';
                table.style.margin = '5% 10%';

                var tableBody = document.createElement('TBODY');
                table.appendChild(tableBody);

                var tr = document.createElement('TR');
                tableBody.appendChild(tr);
                var th1 = document.createElement('TH');
                th1.appendChild(document.createTextNode("Coechipier1"));
                tr.appendChild(th1);

                var th2 = document.createElement('TH');
                th2.appendChild(document.createTextNode("Coechipier2"));
                tr.appendChild(th2);

                for (const [key, value] of Object.entries(JSON.parse(xhr.responseText))) {
                    //console.log(key, value['email']);
                    var tr = document.createElement('TR');
                    tableBody.appendChild(tr);
                    var td1 = document.createElement('TD');
                    td1.appendChild(document.createTextNode(key));
                    tr.appendChild(td1);

                    var td2 = document.createElement('TD');
                    td2.appendChild(document.createTextNode(value['email']));
                    tr.appendChild(td2);
                }

                table.style.width = '80%'
                myTableDiv.appendChild(table);
            } else {
                alert("[Eroare]Server in lucru...");
            }
        }
    };

    xhr.open('GET', url, true);
    xhr.send();
}
function projectBuddiesSVG() {
    var url = 'https://localhost:8443/api/v1/quiz/graphSVG';
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == 200) {
                var graph = document.getElementById("teamsSVG");
                while (graph.firstChild) {
                    graph.removeChild(graph.firstChild);
                }

                var title = document.createElement("h2");
                title.innerText = "Graful coechipierilor";
                title.style.margin = '50px 300px'
                graph.appendChild(title);

                var text = document.createElement("p");
                text.innerText = "Graful jucatorilor este bipartit, putem observa maximum matching.";
                text.style.margin = '50px 0px'
                graph.appendChild(text);

                graph.innerHTML = graph.innerHTML + xhr.responseText;

            }
            else if (xhr.status == 202) {
                connectedComponentsSVG();
            }
            else {
                alert("[Eroare]Server in lucru...");
            }
        }
    };

    xhr.open('GET', url, true);
    xhr.send();
}

function connectedComponentsSVG() {
    document.querySelector("#teamsSVG").style.display = 'none';
    var url = 'https://localhost:8443/api/v1/quiz/comp';
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == 200) {
                var graph = document.getElementById("connectedComponentsSVG");
                while (graph.firstChild) {
                    graph.removeChild(graph.firstChild);
                }

                var title = document.createElement("h2");
                title.innerText = "Graful conex";
                title.style.margin = '50px 350px'
                graph.appendChild(title);

                var text = document.createElement("p");
                text.innerText = "Graful jucatorilor nu este bipartit, asadar vom afla componentele conexe din graful jucatorilor si vom folosi urmatorul algoritm de conectare:";
                text.style.margin = '50px 0px'
                graph.appendChild(text);

                graph.innerHTML = graph.innerHTML + xhr.responseText;

            }
            else {
                alert("[Eroare]Te rugam sa dai un refresh.");
            }
        }
    };

    xhr.open('GET', url, true);
    xhr.send();
}