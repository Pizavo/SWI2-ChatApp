import React, {useEffect, useLayoutEffect, useState} from 'react'
import ChatSelection from './ChatSelection'
import ChatWindow from './ChatWindow'
import {useMediaQuery, useTheme} from '@mui/material'
import {over} from 'stompjs'
import SockJS from 'sockjs-client'
import axios from 'axios'

var stompClient = null
const LOCALHOST_URL = 'http://localhost:8081'

const Menu = (props) => {
	const theme = useTheme()
	const isSmallRes = useMediaQuery(theme.breakpoints.down('sm'))
	
	const [activeChat, setActiveChat] = useState()
	const [privateChats, setPrivateChats] = useState(new Map())
	
	const [isLoading, setIsLoading] = useState(true)
	
	async function handleChatChange(e, newChatId) {
		await setActiveChat(
			await axios.get(LOCALHOST_URL + '/api/chatroom/' + newChatId)
			           .then(result => {
				           return result.data
			           })
			           .catch(error => {
				           console.error(error)
			           }),
		)
	}
	
	useLayoutEffect(() => {
		return () => {
			stompClient.disconnect()
		}
	}, [])
	
	useEffect(() => {
		// pouze pri prvotnim nacteni komponenty
		let sock = new SockJS(LOCALHOST_URL + '/ws')
		stompClient = over(sock)
		stompClient.connect({}, onConnected, onError)
	}, [])
	
	useEffect(() => {
		// kontrola fronty daneho uzivatele
		checkReceivedMessages()
	})
	
	// subscribe na dané topicy
	function onConnected() {
		// private messages
		stompClient.subscribe('/user/' + props.user.username + '/private', onPrivateMessageReceived)
		//privateChats.set(props.user.username, []);
		//setPrivateChats(new Map(privateChats));
		
		// public/group messages
		stompClient.subscribe('/chatroom/public', onPublicMessageReceived)
		//stompClient.subscribe('/chatroom/1', onGroupMessageReceived);
	}
	
	function onError(e) {
		console.error(e)
	}
	
	function checkReceivedMessages() {
		if (isLoading) return
		
		const url = LOCALHOST_URL + '/api/queue'
		const params = new URLSearchParams([['username', props.user.username]])
		try {
			const result = axios.get(url, {params})
			                    .then(result => {
				                    // ulozit zpravy
				                    console.log(result.data)
				
				                    result.data.forEach(i => {
					                    //console.log(privateChats);
					                    privateChats.get(i.room.id).push(i)
					                    setPrivateChats(new Map(privateChats))
					
					                    /*let msgList = [];
					                    i.messages.forEach(j => msgList.push(j));
					                    privateChats.set(i.id, msgList);
					                    setPrivateChats(new Map(privateChats));*/
				                    })
			                    })
		} catch (e) {
			console.log('Error')
		}
	}
	
	function onPrivateMessageReceived(payload) {
		//let payloadData = JSON.parse(payload.body)
		
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
		const url = LOCALHOST_URL + '/api/queue'
		const params = new URLSearchParams([['username', props.user.username]])
		try {
			const result = axios.get(url, {params})
			                    .then(result => {
				                    console.log(result.data)
				
				                    result.data.forEach(i => {
					                    //console.log(privateChats);
					                    privateChats.get(i.room.id).push(i)
					                    setPrivateChats(new Map(privateChats))
					                    //privateChats.get(activeChat).push(i);
					                    //setPrivateChats(new Map(privateChats));
					
					                    //let msgList = [];
					                    //i.messages.forEach(j => msgList.push(j));
					                    //privateChats.set(i.id, msgList);
					                    //setPrivateChats(new Map(privateChats));
				                    })
			                    })
		} catch (e) {
			console.error('Error')
		}
	}
	
	function sendMessage(message) {
		if (activeChat.isPublic) {
			sendStompMessage(message, '/app/message')
		} else if (activeChat.isGroup) {
			sendStompMessage(message, '/app/group-message')
		} else {
			sendStompMessage(message, '/app/private-message')
		}
		
	}
	
	function sendStompMessage(message, destination) {
		if (stompClient) {
			let payloadMsg = {
				senderId: props.user.id,
				chatId: activeChat.id,
				content: message,
				date: new Date().getTime(),
			}
			stompClient.send(destination, {}, JSON.stringify(payloadMsg))
		}
	}
	
	function sendPublicMessage(message) {
		if (stompClient) {
			let payloadMsg = {
				senderId: props.user.id,
				chatId: activeChat.id,
				content: message,
				date: new Date().getTime(),
			}
			stompClient.send('/app/message', {}, JSON.stringify(payloadMsg))
		}
	}
	
	function sendGroupMessage(message) {
		if (stompClient) {
			let payloadMsg = {
				senderId: props.user.id,
				chatId: activeChat.id,
				content: message,
				date: new Date().getTime(),
			}
			stompClient.send('/app/group-message', {}, JSON.stringify(payloadMsg))
		}
	}
	
	function sendPrivateMessage(message) {
		if (stompClient) {
			let payloadMsg = {
				senderId: props.user.id,
				chatId: activeChat.id,
				content: message,
				date: new Date().getTime(),
			}
			stompClient.send('/app/private-message', {}, JSON.stringify(payloadMsg))
		}
	}
	
	return (
		<>
			<ChatSelection activeChat={activeChat} setActiveChat={setActiveChat} handleChatChange={handleChatChange}
			               isSmallRes={isSmallRes} user={props.user} privateChats={privateChats}
			               setPrivateChats={setPrivateChats} isLoading={isLoading} setIsLoading={setIsLoading}/>
			<ChatWindow activeChat={activeChat} isSmallRes={isSmallRes} user={props.user} privateChats={privateChats}
			            isLoading={isLoading} sendMessage={sendMessage}/>
		</>
	)
}

export default Menu