import React from "react";
import {IonButton, IonIcon, IonItem, IonLabel} from '@ionic/react';
import "./Room.css"
import {closeSharp} from "ionicons/icons";
import {RoomProps} from "./RoomProps";

export const Room: React.FC<RoomProps> = ({name, id, onView}) => {
    return (
        <IonItem onClick={() => onView()}>
            <IonLabel class="bold">{name}</IonLabel>
            <IonButton fill="clear" slot="end">
                <IonIcon color="dark" icon={closeSharp}/>
            </IonButton>
        </IonItem>
    );
}

