import {IonButton, IonButtons, IonHeader, IonLabel, IonTitle, IonToolbar} from '@ionic/react';
import React, {useContext} from "react";
import "./Header.css"
import {AuthContext} from "../auth/AuthProvider";
import { Redirect } from 'react-router-dom';



export const Header: React.FC = () => {
    const {token, logout} = useContext(AuthContext);
    const handleLogout = () => {
        logout?.();
        return <Redirect to={{ pathname: "/login" }} />;
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
                    {token != "" && (
                    <IonButton slot="end" class="header-button" onClick={handleLogout}> 
                        <IonLabel class="ion-padding ion-margin">Logout</IonLabel>
                    </IonButton> )}
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

