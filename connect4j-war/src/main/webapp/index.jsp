<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <script src='/_ah/channel/jsapi'></script>
    <style type='text/css'>
        body {
            font-family: 'Helvetica';
        }

        #board {
            width:407px;
            height: 407px;
            margin: 20px auto;
        }

        #display-area {
            text-align: center;
        }

        #this-game {
            font-size: 9pt;
        }

        #winner {
        }

        table {
            border-collapse: collapse;
        }

        td {
            width: 50px;
            height: 50px;
            font-family: "Helvetica";
            font-size: 16pt;
            text-align: center;
            vertical-align: middle;
            margin:0px;
            padding: 0px;
        }

        div.cell {
            float: left;
            width: 50px;
            height: 50px;
            border: none;
            margin: 0px;
            padding: 0px;
        }

        div.mark {
            position: absolute;
            top: 15px;
        }

        div.l {
            border-right: 1pt solid black;
        }

        div.r {
        }

        div.t {
            border-bottom: 1pt solid black;
        }

        div.b {
        }

    </style>
</head>
<body>
<script type='text/javascript'>
    var grid = [];
    for(i = 0; i < 8; i++) {
        for(j = 0; j < 8; j++) {
            grid.push("x"+i+"y"+j);
        }
    }
    var id2pos = {};
    for(var i in grid) {
        id2pos[grid[i]] = i;
    }

    var state = {
        game_key: '${ game_key }',
        me: '${ me }'
        my_google_user: '${ my_google_user }'
    };

    updateGame = function() {
        for (var i in grid) {
            var square = document.getElementById(grid[i]);
            square.innerHTML = state.board[i];
            if (state.winner != '' && state.winningBoard != '') {
                if (state.winningBoard[i] == state.board[i]) {
                    if (state.winner == state.me) {
                        square.style.background = "green";
                    } else {
                        square.style.background = "red";
                    }
                } else {
                    square.style.background = "white";
                }
            }
        }

        var display = {
            'other-player': 'none',
            'your-move': 'none',
            'their-move': 'none',
            'you-won': 'none',
            'you-lost': 'none',
            'board': 'block',
            'this-game': 'block',
        };

        if (!state.userO || state.userO == '') {
            display['other-player'] = 'block';
            display['board'] = 'none';
            display['this-game'] = 'none';
        } else if (state.winner == state.me) {
            display['you-won'] = 'block';
        } else if (state.winner != '') {
            display['you-lost'] = 'block';
        } else if (isMyMove()) {
            display['your-move'] = 'block';
        } else {
            display['their-move'] = 'block';
        }

        for (var label in display) {
            document.getElementById(label).style.display = display[label];
        }
    };

    isMyMove = function() {
        return (state.winner == "") &&
                (state.moveX == (state.userX == state.me));
    }

    myPiece = function() {
        return state.userX == state.me ? 'X' : 'O';
    }

    sendMessage = function(path, opt_param) {
        path += '?g=' + state.game_key;
        if (opt_param) {
            path += '&' + opt_param;
        }
        var xhr = new XMLHttpRequest();
        xhr.open('POST', path, true);
        xhr.send();
    };

    moveInSquare = function(id) {
        if (isMyMove() && state.board[id2pos[id]] == ' ') {
            sendMessage('/move', 'i=' + id2pos[id]);
        }
    }

    highlightSquare = function(id) {
        if (state.winner != "") {
            return;
        }
        for (var i in grid) {
            if (grid[i] == id  && isMyMove()) {
                if (state.board[i] = ' ') {
                    color = 'lightBlue';
                } else {
                    color = 'lightGrey';
                }
            } else {
                color = 'white';
            }

            document.getElementById(grid[i]).style['background'] = color;
        }
    }

    onOpened = function() {
        sendMessage('/opened');
    };

    onMessage = function(m) {
        newState = JSON.parse(m.data);
        state.board = newState.board || state.board;
        state.userX = newState.userX || state.userX;
        state.userO = newState.userO || state.userO;
        state.moveX = newState.moveX;
        state.winner = newState.winner || "";
        state.winningBoard = newState.winningBoard || "";
        updateGame();
    }

    openChannel = function() {
        var token = '${ token }';
        var channel = new goog.appengine.Channel(token);
        var handler = {
            'onopen': onOpened,
            'onmessage': onMessage,
            'onerror': function() {},
            'onclose': function() {}
        };
        var socket = channel.open(handler);
        socket.onopen = onOpened;
        socket.onmessage = onMessage;
    }

    initialize = function() {
        openChannel();
        var i;
        for (i in grid) {
            var square = document.getElementById(grid[i]);
            square.onmouseover = new Function('highlightSquare("' + grid[i] + '")');
            square.onclick = new Function('moveInSquare("' + grid[i] + '")');
        }
        onMessage({data: '${ initial_message }'});
    }

    setTimeout(initialize, 100);

</script>
<div id='display-area'>
    <h2>Channel-based connect4</h2>
    <div id='other-player' style='display:none'>
        Waiting for another player to join.<br>
        Send them this link to play:<br>
        <div id='game-link'><a href='${ game_link }'>${ game_link }</a></div>
    </div>
    <div id='your-move' style='display:none'>
        Your move! Click a square to place your piece.
    </div>
    <div id='their-move' style='display:none'>
        Waiting for other player to move...
    </div>
    <div id='you-won'>
        You won this game!
    </div>
    <div id='you-lost'>
        You lost this game.
    </div>
    <div id="board" style="display: block;">
        <div class="t l cell"><table><tbody><tr><td id="x0y0"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x0y1"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x0y2"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x0y3"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x0y4"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x0y5"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x0y6"></td></tr></tbody></table></div>
        <div class="t r cell"><table><tbody><tr><td id="x0y7"></td></tr></tbody></table></div>
        <!-- one row -->
        <div class="t l cell"><table><tbody><tr><td id="x1y0"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x1y1"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x1y2"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x1y3"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x1y4"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x1y5"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x1y6"></td></tr></tbody></table></div>
        <div class="t r cell"><table><tbody><tr><td id="x1y7"></td></tr></tbody></table></div>
        <!-- one row -->
        <div class="t l cell"><table><tbody><tr><td id="x2y0"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x2y1"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x2y2"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x2y3"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x2y4"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x2y5"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x2y6"></td></tr></tbody></table></div>
        <div class="t r cell"><table><tbody><tr><td id="x2y7"></td></tr></tbody></table></div>
        <!-- one row -->
        <div class="t l cell"><table><tbody><tr><td id="x3y0"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x3y1"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x3y2"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x3y3"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x3y4"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x3y5"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x3y6"></td></tr></tbody></table></div>
        <div class="t r cell"><table><tbody><tr><td id="x3y7"></td></tr></tbody></table></div>
        <!-- one row -->
        <div class="t l cell"><table><tbody><tr><td id="x4y0"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x4y1"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x4y2"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x4y3"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x4y4"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x4y5"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x4y6"></td></tr></tbody></table></div>
        <div class="t r cell"><table><tbody><tr><td id="x4y7"></td></tr></tbody></table></div>
        <!-- one row -->
        <div class="t l cell"><table><tbody><tr><td id="x5y0"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x5y1"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x5y2"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x5y3"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x5y4"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x5y5"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x5y6"></td></tr></tbody></table></div>
        <div class="t r cell"><table><tbody><tr><td id="x5y7"></td></tr></tbody></table></div>
        <!-- one row -->
        <div class="t l cell"><table><tbody><tr><td id="x6y0"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x6y1"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x6y2"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x6y3"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x6y4"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x6y5"></td></tr></tbody></table></div>
        <div class="t l cell"><table><tbody><tr><td id="x6y6"></td></tr></tbody></table></div>
        <div class="t r cell"><table><tbody><tr><td id="x6y7"></td></tr></tbody></table></div>
        <!-- one row -->
        <div class="b l cell"><table><tbody><tr><td id="x7y0"></td></tr></tbody></table></div>
        <div class="b l cell"><table><tbody><tr><td id="x7y1"></td></tr></tbody></table></div>
        <div class="b l cell"><table><tbody><tr><td id="x7y2"></td></tr></tbody></table></div>
        <div class="b l cell"><table><tbody><tr><td id="x7y3"></td></tr></tbody></table></div>
        <div class="b l cell"><table><tbody><tr><td id="x7y4"></td></tr></tbody></table></div>
        <div class="b l cell"><table><tbody><tr><td id="x7y5"></td></tr></tbody></table></div>
        <div class="b l cell"><table><tbody><tr><td id="x7y6"></td></tr></tbody></table></div>
        <div class="b r cell"><table><tbody><tr><td id="x7y7"></td></tr></tbody></table></div>
    </div>
    <div id='this-game' float='top'>
        Quick link to this game: <span id='this-game-link'><a href='${ game_link }'>${ game_link }</a></span>
    </div>
</div>
</body>
</html>