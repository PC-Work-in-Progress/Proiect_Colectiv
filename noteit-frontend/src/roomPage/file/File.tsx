import { IonButton, IonCardHeader, IonCardSubtitle, IonCardTitle, IonIcon, IonItem, IonLabel } from "@ionic/react";
import { closeSharp } from "ionicons/icons";
import React from "react";
import { FileProps } from "./FileProps";

export const MyFile: React.FC<FileProps> = ({    fileId,name,type,username,date,URL,size, onView}) => {
    return (
        <>
            <IonItem onClick={() => onView?.()}>
                <IonLabel class="bold">{name}</IonLabel>
                <IonButton fill="clear" slot="end">
                    <IonIcon color="dark" icon={closeSharp}/>
                </IonButton>
            </IonItem>
        </>
    );
}