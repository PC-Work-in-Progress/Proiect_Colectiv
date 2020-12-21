import React from "react";
import {IonCheckbox, IonItem, IonLabel} from '@ionic/react';
import "./Tag.css"
import {TagCheck} from "./Discover";

export const Tag: React.FC<TagCheck> = ({name, id, onCheck, checked}) => {
    return (
        <IonItem>
            <IonLabel class="bold">{name}</IonLabel>
            <IonCheckbox onIonChange={() => onCheck?.()} slot="end" value={name} checked={checked}/>
        </IonItem>
    );
}

