import React from "react";
import {IonItem, IonLabel} from '@ionic/react';
import "./Room.css"
import {RoomProps} from "./RoomProps";

export const Room: React.FC<RoomProps> = ({name, id, onView}) => {
    return (
        <IonItem button={true} onClick={() => onView?.()}>
            <IonLabel class="bold">{name}</IonLabel>
            {/*<IonButton fill="clear" slot="end">*/}
            {/*    <IonIcon color="dark" icon={closeSharp}/>*/}
            {/*</IonButton>*/}
        </IonItem>
    );
}

