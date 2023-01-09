import React, { useState, useEffect } from 'react';
import { Tabs, Tab, Box, Typography, Avatar, IconButton, Grid } from '@mui/material';
import { styled } from '@mui/material/styles';
import AddCommentIcon from '@mui/icons-material/AddComment';
import axios from 'axios';

const LOCALHOST_URL = 'http://localhost:8081';

const ChatTabs = styled(Tabs)({
    '& .MuiTabs-indicator': {
        backgroundColor: 'transparent',
    },
    '& .MuiTab-root.Mui-selected': {
        backgroundColor: '#2b253c !important',
        color: 'white',

    },
    '& .MuiButtonBase-root.MuiTab-root': {
        borderRadius: 10,
        ':hover': {
            backgroundColor: '#302f32'
        }
    },
});

const ChatSelection = (props) => {

    const [chatRooms, setChatRooms] = useState([]);

    useEffect((e) => {
        fetchChatRooms(e);
    }, []);

    async function fetchChatRooms(e) {
        const url = LOCALHOST_URL + '/chatrooms';
        const params = new URLSearchParams([['username', props.user.username]]);
        try {
            const result = await axios.get(url, { params })
                .then(result => {
                    result.data.forEach(i => setChatRooms((chatRooms) => [...chatRooms, { chatId: i.chatId, chatName: i.chatName, messages: i.messages }]));
                    try {
                        props.handleChatChange(e, result.data[0]?.chatId);
                    } catch (e) {
                        console.log("No chats available!");
                    }

                    try {
                        result.data.forEach(i => {
                            let msgList = [];
                            i.messages.forEach(j => msgList.push(j));
                            props.privateChats.set(i.chatId, msgList);
                            props.setPrivateChats(new Map(props.privateChats));
                        });
                    } catch (e) {
                        console.log("No messages available!");
                    }
                    props.setIsLoading(false);
                });
        } catch (e) {
            console.log('Error in fetching chat rooms!');
        }
    }

    function but() {
        console.log(props.privateChats);
    }

    return (
        <div>
            {props.isLoading ? (
                <>Loading...</>
            ) : (
                <Box
                    sx={{
                        bgcolor: '#1b1b1d', position: 'absolute', left: '0', right: '1',
                        padding: '10px', borderRight: 1, borderColor: '#999b9d', height: 'calc(100% - 65px - 20px)',
                        ...(props.isSmallRes === true && {
                            paddingRight: '0px',
                            width: '70px'
                        }),
                        ...(props.isSmallRes === false && {
                            width: '300px'
                        })
                    }}
                >
                    <Grid container direction='row' justifyContent='space-between' alignItems='center'>
                        {!props.isSmallRes ? (
                            <Grid item>
                                <Typography variant='h5'>
                                    Chats
                                </Typography>
                            </Grid>
                        ) : (
                            <></>
                        )}
                        <Grid item sx={{
                            ...(props.isSmallRes === true && {
                                marginLeft: 'auto',
                                marginRight: 'auto'
                            })
                        }}
                        >
                            <IconButton sx={{ paddingLeft: '0px' }} onClick={but}>
                                <AddCommentIcon sx={{ color: 'white' }}></AddCommentIcon>
                            </IconButton>
                        </Grid>
                    </Grid>
                    <ChatTabs
                        value={props.activeChat}
                        onChange={props.handleChatChange}
                        orientation='vertical'
                    >
                        {chatRooms.map((chatRoom) =>
                            <Tab
                                key={chatRoom.chatId}
                                value={chatRoom.chatId}
                                label={!props.isSmallRes ? (
                                    chatRoom.chatName
                                ) : (
                                    ""
                                )}
                                sx={{ color: 'white', justifyContent: 'left', paddingLeft: '10px' }}
                                icon={<Avatar sx={{ bgcolor: '#9c49f3', color: 'black' }}><div className='MyFont'>{chatRoom.chatName.charAt(0).toUpperCase()}</div></Avatar>}
                                iconPosition='start'
                            />
                        )}
                    </ChatTabs>
                </Box>
            )}
        </div>
    )
}

export default ChatSelection