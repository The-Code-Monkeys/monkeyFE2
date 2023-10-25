const express = require('express');
const app = express();
const http = require('http');
const server = http.createServer(app);
const { Server } = require("socket.io");
const io = new Server(server);

var users = [];
var currUser = null;

server.listen(3000, () => {
    console.log("listening on 3000");
});

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/test.html');
});

io.on('connection', (socket) => {
    console.log('a user connected');
    users.push(socket.id);
    console.log('Current queue:', users);
    io.emit('waitlist-update', users.indexOf(socket.id));

    socket.on('connect-to-bot', () => {
        console.log("sent out");
        if(socket.id == users[0]){
            currUser = users.shift();
            io.to(currUser).emit('verified');
        }
        else{
            io.to(socket.id).emit('denied');
        }
    })

    socket.on('up', () => {
        console.log("Moving bot up");
    });

    socket.on('left', () => {
        console.log("Moving bot left");
    });
    
    socket.on('right', () => {
        console.log("Moving bot right");
    });

    socket.on('down', () => {
        console.log("Moving bot down");
    });

    socket.on('disconnect', () => {
        console.log('user disconnected');
        users.splice(users.indexOf(socket.id), 1);
        console.log('Current queue:', users);
        io.emit('waitlist-update', users.indexOf(currUser));
    });
});
