const express = require('express');
const app = express();
const http = require('http');
const server = http.createServer(app);
const { Server } = require("socket.io");
const io = new Server(server);

var users = {};

server.listen(3000, () => {
    console.log("listening on 3000");
});

app.get('/', (req, res) => {
    res.sendFile(__dirname + '/index.html');
});

io.on('connection', (socket) => {
    console.log('a user connected');
    users[socket.id] = {
        ID : socket.id,
    }
    currUser = Object.keys(users)[0];
    io.emit('waitlist-update', Object.keys(users).length-1);

    socket.on('connect-to-bot', () => {
        console.log("sent out");
        if(socket.id == currUser){
            io.broadcast.to(socket.id).emit('verified');
        }
        else{
            io.broadcast.to(socket.id).emit('denied');
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
        delete users[socket.id];
        io.emit('waitlist-update', Object.keys(users).length-1);
    });
  });
