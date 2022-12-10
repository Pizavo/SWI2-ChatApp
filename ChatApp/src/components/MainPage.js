import React, { useEffect, useLayoutEffect, useState } from 'react';
import ChatSelection from './ChatSelection';
import ChatWindow from './ChatWindow';
import { useTheme, useMediaQuery } from '@mui/material';
import { over } from 'stompjs';
import SockJS from 'sockjs-client';
import axios from 'axios';

var stompClient = null;
const LOCALHOST_URL = 'http://localhost:8081';

const Menu = (props) => {

  const theme = useTheme();
  const isSmallRes = useMediaQuery(theme.breakpoints.down('sm'));

  const [activeChat, setActiveChat] = useState();
  const [privateChats, setPrivateChats] = useState(new Map());

  const [isLoading, setIsLoading] = useState(true);

  function handleChatChange(e, newChat) {
    // TODO: někde budu muset řešit znovunačtení databáze (fetchChatRooms)
    setActiveChat(newChat);
  }

  useLayoutEffect(() => {
    return () => {
      stompClient.disconnect();
    }
  }, []);

  useEffect(() => {
    // pouze pri prvotnim nacteni komponenty
    let sock = new SockJS(LOCALHOST_URL + "/ws");
    stompClient = over(sock);
    stompClient.connect({}, onConnected, onError);
  }, []);

  useEffect(() => {
    // kontrola fronty daneho uzivatele
    checkReceivedMessages();
  });

  // subscribe na dané topicy 
  function onConnected() {
    // private messages
    stompClient.subscribe('/user/' + props.user.username + '/private', onPrivateMessageReceived);
    //privateChats.set(props.user.username, []);
    //setPrivateChats(new Map(privateChats));

    // public/group messages
    stompClient.subscribe('/chatroom/public', onPublicMessageReceived);
    //stompClient.subscribe('/chatroom/1', onGroupMessageReceived);
  }

  function onError(e) {
    console.log(e);
  }

  function checkReceivedMessages() {
    if (isLoading) return;

    const url = LOCALHOST_URL + '/api/queue';
    const params = new URLSearchParams([['username', props.user.username]]);
    try {
      const result = axios.get(url, { params })
        .then(result => {
          // ulozit zpravy
          console.log(result.data);

          result.data.forEach(i => {
            //console.log(privateChats);
            privateChats.get(i.chatRoom.chatId).push(i);
            setPrivateChats(new Map(privateChats));

            //let msgList = [];
            //i.messages.forEach(j => msgList.push(j));
            //privateChats.set(i.chatId, msgList);
            //setPrivateChats(new Map(privateChats));
          });
        });
    } catch (e) {
      console.log("Error");
    }
  }

  function onPrivateMessageReceived(payload) {
    let payloadData = JSON.parse(payload.body);

    /*
    if (privateChats.get(payloadData.senderName)) {
      privateChats.get(payloadData.senderName).push(payloadData);
      setPrivateChats(new Map(privateChats));
    } else {
      let list = [];
      list.push(payloadData);
      privateChats.set(payloadData.senderName, list);
      setPrivateChats(new Map(privateChats));
    }
    */
  }

  function onPublicMessageReceived(payload) {
    // Vyzvednout zprávu z public-queue-username
    const url = LOCALHOST_URL + '/api/queue';
    const params = new URLSearchParams([['username', props.user.username]]);
    try {
      const result = axios.get(url, { params })
        .then(result => {
          // ulozit zpravy
          console.log(result.data);

          result.data.forEach(i => {
            //console.log(privateChats);
            privateChats.get(i.chatRoom.chatId).push(i);
            setPrivateChats(new Map(privateChats));
            //privateChats.get(activeChat).push(i);
            //setPrivateChats(new Map(privateChats));

            //let msgList = [];
            //i.messages.forEach(j => msgList.push(j));
            //privateChats.set(i.chatId, msgList);
            //setPrivateChats(new Map(privateChats));
          });
        });
    } catch (e) {
      console.log("Error");
    }
  }

  function sendMessage(message) {
    // odeslat nejakou message
    sendPublicMessage(message);
  }

  function sendPublicMessage(message) {
    if (stompClient) {
      let payloadMsg = {
        senderName: props.user.username,
        content: message,
        date: new Date().getTime()
      };
      stompClient.send('/app/message', {}, JSON.stringify(payloadMsg));
    }
  }

  function sendGroupMessage(message) {
    if (stompClient) {
      let payloadMsg = {
        senderName: props.user.username,
        receiverGroupId: activeChat,
        content: message,
        date: new Date().getTime()
      };
      stompClient.send('/app/group-message', {}, JSON.stringify(payloadMsg));
    }
  }

  function sendPrivateMessage(message) {
    if (stompClient) {
      let payloadMsg = {
        senderName: props.user.username,
        receiverName: "ToDo",
        content: message,
        date: new Date().getTime()
      };
      stompClient.send('/app/private-message', {}, JSON.stringify(payloadMsg));
    }
  }

  return (
    <>
      <ChatSelection activeChat={activeChat} handleChatChange={handleChatChange} isSmallRes={isSmallRes} user={props.user} privateChats={privateChats} setPrivateChats={setPrivateChats} isLoading={isLoading} setIsLoading={setIsLoading} />
      <ChatWindow activeChat={activeChat} isSmallRes={isSmallRes} user={props.user} privateChats={privateChats} isLoading={isLoading} sendMessage={sendMessage} />
    </>
  )
}

export default Menu