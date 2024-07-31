// index.js
import dotenv from 'dotenv';
dotenv.config(!!process.env.CONFIG ? {path: process.env.CONFIG} : {});

import express from 'express';
import bodyParser from 'body-parser';
import http from 'http';
import { OpenVidu } from 'openvidu-node-client';
import cors from 'cors';

const app = express();

// Environment variable: PORT where the node server is listening
const SERVER_PORT = process.env.SERVER_PORT || 5713;
// Environment variable: URL where our OpenVidu server is listening
const OPENVIDU_URL = process.env.OPENVIDU_URL || 'http://localhost:4443';
// Environment variable: secret shared with our OpenVidu server
const OPENVIDU_SECRET = process.env.OPENVIDU_SECRET || 'MY_SECRET';

// Enable CORS support
app.use(
  cors({
    origin: "*",
  })
);

const server = http.createServer(app);
const openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);

// Allow application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: true }));
// Allow application/json
app.use(bodyParser.json());

// Serve static resources if available
app.use(express.static('public'));

// Serve application
server.listen(SERVER_PORT, () => {
  console.log("Application started on port: ", SERVER_PORT);
  console.warn('Application server connecting to OpenVidu at ' + OPENVIDU_URL);
});

app.post("/api/sessions", async (req, res) => {
  const session = await openvidu.createSession(req.body);
  res.send(session.sessionId);
});

app.post("/api/sessions/:sessionId/connections", async (req, res) => {
  const session = openvidu.activeSessions.find(
    (s) => s.sessionId === req.params.sessionId
  );
  if (!session) {
    res.status(404).send();
  } else {
    const connection = await session.createConnection(req.body);
    res.send(connection.token);
  }
});

process.on('uncaughtException', err => console.error(err));
