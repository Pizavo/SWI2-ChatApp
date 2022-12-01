import './MenuPage.css';
import React, {useEffect, useState} from "react";
import {Button} from "@mui/material";
import {over} from 'stompjs';
import SockJS from 'sockjs-client';

var stompClient = null;

const MenuPage = (props) => {

    const [activeChat, setActiveChat] = useState();
    const [privateChats, setPrivateChats] = useState(new Map());

    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        let sock = new SockJS('http://localhost:8081/ws');
        stompClient = over(sock);
        stompClient.connect({}, onConnected, onError);
    }, [])

    function onConnected() {
        stompClient.subscribe('/user/' + props.user.username + '/private', onPrivateMessageReceived);
        stompClient.subscribe('/chatroom/public', onPublicMessageReceived);
    }

    function onError(e) {
        console.log("Error in connecting through web sockets! " + e)
    }

    function onPublicMessageReceived(payload) {
        // handle public message
    }


    function onPrivateMessageReceived(payload) {
        // handle private message
    }

    function logoutUser() {
        props.setUserToken("");
    }

    return (
        <div className="MenuPage">
            <h3>Hello, {props.username}</h3>
            <Button
                variant='contained'
                type='submit'
                onClick={logoutUser}
            >
                Log out
            </Button>
        </div>
    )
}

export default MenuPage