import React from "react";
import {IonCardHeader, IonCardSubtitle, IonCardTitle, IonChip, IonIcon} from '@ionic/react';
import "./User.css"
import {personCircle} from "ionicons/icons";
import {UserProps} from "./UserProps";

interface WithRank extends UserProps {
    rank: string;
}

export const User: React.FC<WithRank> = ({fullName, userName, email, rank}) => {
    return (
        <>
            <IonCardHeader>
                <div className="user-div">
                    <IonIcon class="user-icon" icon={personCircle}/>
                    <div className="user-info">
                        <IonCardTitle class="user-name">{userName}</IonCardTitle>
                        <IonCardSubtitle class="user-full-name">{fullName}</IonCardSubtitle>
                        <div className="user-email">{email}</div>
                        <IonChip>{ rank} </IonChip>
                    </div>
                </div>
            </IonCardHeader>
        </>
    );
}

