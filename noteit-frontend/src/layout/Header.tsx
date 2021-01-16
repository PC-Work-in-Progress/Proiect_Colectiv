import {IonAlert, IonButton, IonButtons, IonHeader, IonIcon, IonLabel, IonTitle, IonToolbar} from '@ionic/react';
import React, {useContext, useEffect, useState} from "react";
import "./Header.css"
import {AuthContext} from "../auth/AuthProvider";
import { Redirect } from 'react-router-dom';
import { notifications, notificationsOffCircleOutline} from 'ionicons/icons';
import { GetNotifications } from './notificationApi';



export const Header: React.FC = () => {
    const {token, logout} = useContext(AuthContext);
    const [modal, setShowModal] = useState(false);
    let message = "Notificari:";
    const handleLogout = () => {
        logout?.();
      }


    const [notify, setNotify] = useState(false)
    let notifications = [];

    useEffect(GetNotificationsEffect, [modal, token])

    function GetNotificationsEffect() {
        let canceled = false;
        console.log(token);
        GetNotificationsAsync();
        return () => {
            canceled = true;
        }

        async function GetNotificationsAsync() {

            

            await GetNotifications(token).then(result => {
                for(var notification in result) {
                    message = "";
                    message += notification + '\n';
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
                            <IonIcon icon={notificationsOffCircleOutline} onClick={e => setShowModal(true)}></IonIcon>
                        </IonButton>
                        {modal && (
                        <IonAlert
                                        isOpen={modal}
                                        onDidDismiss={() => setShowModal(false)}
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

