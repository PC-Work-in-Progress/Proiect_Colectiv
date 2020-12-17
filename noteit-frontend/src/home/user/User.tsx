import React from "react";
import {IonCardHeader, IonCardSubtitle, IonCardTitle, IonIcon} from '@ionic/react';
import "./User.css"
import {personCircle} from "ionicons/icons";
import {UserProps} from "./UserProps";

export const User: React.FC<UserProps> = ({fullName, userName, email}) => {
    return (
        <>
            <IonCardHeader>
                <div className="user-div">
                    <IonIcon class="user-icon" icon={personCircle}/>
                    <div className="user-info">
                        <IonCardTitle class="user-name">{userName}</IonCardTitle>
                        <IonCardSubtitle class="user-full-name">{fullName}</IonCardSubtitle>
                        <div className="user-email">{email}</div>
                    </div>
                </div>
            </IonCardHeader>
        </>
    );
}

