import {IonAlert, IonButton, IonButtons, IonHeader, IonIcon, IonLabel, IonTitle, IonToolbar} from '@ionic/react';
import React, {useContext, useState} from "react";
import "./Header.css"
import {AuthContext} from "../auth/AuthProvider";
import { Redirect } from 'react-router-dom';
import { notifications, notificationsOffCircleOutline} from 'ionicons/icons';
import { GetNotifications } from './notificationApi';



export const Header: React.FC = () => {
    const {token, logout} = useContext(AuthContext);
    const [modal, setShowModal] = useState(false);

    const handleLogout = () => {
        logout?.();
      }


    const [notify, setNotify] = useState(false)
    let notifications = [];

    function GetNotificationsEffect() {
        let canceled = false;
        GetNotificationsAsync();
        return () => {
            canceled = true;
        }

        async function GetNotificationsAsync() {
            await GetNotifications(token).then(result => {
                for(var notification in result) {
                    console.log(notification);
                }
            });

        }
    }
    

    return (
        <>
            <IonHeader>
                <IonToolbar color="primary" class="header">
                    <IonButtons slot="start">
                        <IonButton class="header-name-button" href="/home">
                            <IonTitle class="header-title ion-padding">NoteIt</IonTitle>
                        </IonButton>
                    </IonButtons>

                                      
                    {token !== "" && (
                    <IonButtons slot="end" >
                        <IonButton>
                            <IonIcon icon={notificationsOffCircleOutline}></IonIcon>
                        </IonButton>
                        <IonAlert
                                        isOpen={modal}
                                        onDidDismiss={() => setShowModal(false)}
                                        cssClass='modal'
                                        message={'Make sure it\'s at least 16 characters OR 8 including a number, a lowercase\n' +
                                        '                                            letter and a special character'}
                                        buttons={['OK']}
                                    />
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

