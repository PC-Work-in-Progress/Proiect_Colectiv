import React from "react";
import {IonItem, IonLabel, IonNote} from '@ionic/react';
import "./Notification.css"
import {NotificationProps} from "./NotificationProps";

export const Notification: React.FC<NotificationProps> = ({onView, userName, roomName, fileId, fileName, time}) => {
    return (
        <IonItem onClick={() => onView()}>
            <IonLabel>
                <span className="title bold">{fileName}</span>
                <div className="userName">{userName}</div>
            </IonLabel>
            <IonLabel>posted in <span className="room">{roomName}</span></IonLabel>
            <IonNote slot="end">1 hour ago</IonNote>
        </IonItem>
    );
}

