import React from "react";
import {IonChip, IonItem, IonLabel, IonNote} from '@ionic/react';
import "./Notification.css"
import {NotificationProps} from "./NotificationProps";

export const Notification: React.FC<NotificationProps> = ({onView, userName, roomName, fileId, fileName, fileDate, tags}) => {
    return (
        <IonItem key={fileId} onClick={() => onView()}>
            <IonLabel>
                <span className="title bold">{fileName}</span>
                {tags.slice(0, 3).map(tag =>
                    <IonChip key={fileId + tag} class="tag">{tag}</IonChip>
                )}
                <div className="userName">{userName}</div>
            </IonLabel>
            <IonLabel>posted in <span className="room">{roomName}</span></IonLabel>
            <IonNote slot="end">{fileDate}</IonNote>
        </IonItem>
    );
}

