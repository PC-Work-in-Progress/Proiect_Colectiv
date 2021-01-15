import React from "react";
import {IonItem, IonLabel} from '@ionic/react';
import "./Room.css"
import {RoomProps} from "../home/room";

export const Room: React.FC<RoomProps> = ({name, id, onView}) => {
    return (
        <IonItem button={true} onClick={() => onView?.()}>
            <IonLabel class="bold">{name}</IonLabel>
        </IonItem>
    );
}

