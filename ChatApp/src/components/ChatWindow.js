import {Box, IconButton, Input} from '@mui/material'
import {styled} from '@mui/material/styles'
import {SendRounded} from '@mui/icons-material'
import React, {useRef, useState} from 'react'

const TextInput = styled(Input)({
	color: 'white',
	backgroundColor: '#39393c',
	borderRadius: '20px',
	padding: '5px 15px',
	width: '100%',
	minHeight: '40px',
	'&.MuiInput-underline:after': {
		borderColor: 'transparent',
	},
	'&.MuiInput-underline:before': {
		borderColor: 'transparent',
	},
	'&:not(.Mui-disabled):hover::before': {
		borderColor: 'transparent',
	},
})

const SendButton = styled(IconButton)({
	':hover': {
		background: '#39393c',
	},
	float: 'right',
})

const ChatWindow = (props) => {
	const [message, setMessage] = useState('')
	
	const formRef = useRef(null)
	
	function send(e) {
		e.preventDefault()
		props.sendMessage(message)
		formRef.current.reset()
		setMessage('')
	}
	
	function onKeyDown(e) {
		if (e.keyCode === 13 && !e.shiftKey) {
			send(e)
		}
	}
	
	function formatDate(dateTime) {
		const options = {year: 'numeric', month: 'numeric', day: 'numeric', hour: 'numeric', minute: 'numeric'}
		return new Date(dateTime).toLocaleDateString(undefined, options)
	}
	
	return (
		<>
			{props.isLoading ? (
				<></>
			) : (
				<Box sx={{
					position: 'absolute', padding: '10px',
					...(props.isSmallRes === true && {
						left: '80px',
						width: 'calc(100% - 110px)',
					}),
					...(props.isSmallRes === false && {
						left: '320px',
						width: 'calc(100% - 340px)',
					}),
					textAlign: 'center',
					height: 'calc(100% - 65px - 20px)',
				}}>
					<Box sx={{position: 'relative', height: '100%'}}>
						<Box sx={{position: 'absolute', bottom: '0', width: '100%'}}>
							<form onSubmit={send} ref={formRef}>
								<div className="MessageInput">
									<SendButton type="submit">
										<SendRounded color="secondary"/>
									</SendButton>
									<div>
										<TextInput
											multiline
											placeholder="Aa"
											onChange={e => {
												setMessage(e.target.value)
											}}
											onKeyDown={onKeyDown}
										/>
									</div>
								</div>
							</form>
						</Box>
						<Box>
							{[...props.privateChats.get(props.activeChat.id).map((msg, index) => (
								<Box key={index} sx={{paddingBottom: '10px', width: '100%', overflow: 'auto'}}>
									{msg.user.id === props.user.id ? (
										<div>
											<div style={{
												padding: '10px',
												float: 'right',
												clear: 'both',
												textAlign: 'left',
												fontSize: '11px',
											}}>
												<span
													style={{fontSize: '13px'}}><b>{msg.user.username}</b></span> {formatDate(msg.sendTime)}
											</div>
											<Box sx={{
												padding: '10px',
												float: 'right',
												clear: 'both',
												textAlign: 'left',
												backgroundColor: '#7505ff',
												borderRadius: '15px',
												maxWidth: '75%',
											}}>
												{msg.content}
											</Box>
										</div>
									) : (
										<div>
											<div style={{
												padding: '10px',
												float: 'left',
												clear: 'both',
												textAlign: 'left',
												fontSize: '11px',
											}}>
												<span
													style={{fontSize: '13px'}}><b>{msg.user.username}</b></span> {formatDate(msg.sendTime)}
											</div>
											<Box sx={{
												padding: '10px',
												float: 'left',
												clear: 'both',
												textAlign: 'left',
												backgroundColor: '#494157',
												borderRadius: '15px',
												maxWidth: '75%',
											}}>
												{msg.content}
											</Box>
										</div>
									
									)}
								</Box>
							))]}
							<Box sx={{paddingBottom: '10px', width: '100%', overflow: 'auto'}}>
							</Box>
						</Box>
					</Box>
				</Box>
			)}
		
		</>
	)
}

export default ChatWindow