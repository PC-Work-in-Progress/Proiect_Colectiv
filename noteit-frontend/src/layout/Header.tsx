import {IonAlert, IonButton, IonButtons, IonHeader, IonIcon, IonLabel, IonTitle, IonToolbar} from '@ionic/react';
import React, {useContext, useEffect, useState} from "react";
import "./Header.css"
import {AuthContext} from "../auth/AuthProvider";
import {notifications, notificationsOffCircleOutline} from 'ionicons/icons';
import {GetNotifications, NotificationMessageProps, ReadNotification} from './notificationApi';


export const Header: React.FC = () => {
    const {token, logout} = useContext(AuthContext);
    const [modal, setShowModal] = useState(false);
    const [message, setMessage] = useState("");
    const handleLogout = () => {
        logout?.();
      }


    const [notify, setNotify] = useState(false);

    useEffect(GetNotificationsEffect, [modal, token])

    const [notificationList, setNotificationList] = useState<NotificationMessageProps[]>();

    var n = async function(token: string, nid:number) {
        if(notificationList) {
            if(notificationList[nid]) {
                await ReadNotification(token, notificationList[nid].id);
            }
        }
    }

    function readNotificationList() {
        let i;

        if(notificationList) {
            for(i = 0; i < notificationList.length; ++i ) {
                if (notificationList[i].viewed === 0) {
                   n(token, i);
                }
            }
        }
        setNotify(false);
    }

    function GetNotificationsEffect() {
        let canceled = false;
        GetNotificationsAsync();
        return () => {
            canceled = true;
        }

        async function GetNotificationsAsync() {

            if(notify === false) {
                if(token !== "") {
                const result = await GetNotifications(token)
                
                let i;
                let newMessage = "Notifications: \n";
                for(i = 0; i < result.length; ++i ) {
                    if ( result[i].viewed === 0 ) {
                        setNotify(true)
                        newMessage += result[i].message + '\n';
                    }
                
                setMessage(newMessage);
                setNotificationList(result);
                }
            }
        }

        }
    }
    

    return (
        <>
            <IonHeader>
                <IonToolbar color="light" class="header">
                    <IonButtons slot="start">
                        <IonButton class="header-name-button" href="/home">
                            <img className="logo" src="assets/logo.png" alt="ceva"/>
                            <IonTitle color="dark" class="header-title ion-padding">NoteIt</IonTitle>
                        </IonButton>
                    </IonButtons>

                                      
                    {token !== "" && (
                    <IonButtons slot="end" >
                        <IonButton onClick = {() => setShowModal(true) }>
                            {notify === true && (
                            <IonIcon icon={notifications}></IonIcon>
                            )}
                            {notify === false && (
                            <IonIcon icon={notificationsOffCircleOutline}></IonIcon>
                            )}
                        </IonButton>
                        {modal && (
                        <IonAlert
                                        isOpen={modal}
                                        onDidDismiss={() => { setShowModal(false); readNotificationList();}}
                                        cssClass='modal'
                                        message={message}
                                        buttons={['OK']}
                                    />  
                        )}
                        <IonButton class="header-button" href="/discover">
                            <IonLabel class="ion-padding">Discover rooms</IonLabel>
                        </IonButton>
                        <IonButton class="header-button" href="/user">
                            <IonLabel class="ion-padding">User</IonLabel>
                        </IonButton>
                        <IonButton class="header-button"  onClick={handleLogout} href="/login">
                            <IonLabel class="ion-padding">Logout</IonLabel>
                        </IonButton>
                    </IonButtons> )}
                    {token === "" && (<IonButtons slot="end">
                        <IonButton class="header-button" href="/login">
                            <IonLabel class="ion-padding ion-margin">Login</IonLabel>
                        </IonButton>
                        <IonButton class="header-button" href="/signup">
                            <IonLabel class="ion-padding">Sign up</IonLabel>
                        </IonButton>
                    </IonButtons>)}
                </IonToolbar>
            </IonHeader>
        </>
    )
};

