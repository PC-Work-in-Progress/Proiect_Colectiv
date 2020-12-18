import { IonButton, IonIcon, IonItem, IonLabel, IonList } from "@ionic/react";
import { checkmark, closeSharp } from "ionicons/icons";
import React from "react";
import { FileProps } from "./FileProps";

export const MyFile: React.FC<FileProps> = ({    fileId,name,type,username,date,URL,size, onView, onReview, isAdmin}) => {

    return (
        <>
            <IonItem>
                <IonLabel class="bold" onClick={() => onView?.()}>{name} </IonLabel>
                { isAdmin === true && (<IonList>
                <IonButton fill="clear" slot="end" onClick={() => { onReview?.(fileId,"accept")}}>
                     <IonIcon color="dark" icon={checkmark} />
                </IonButton>
                <IonButton fill="clear" slot="end" onClick={() => { onReview?.(fileId,"deny")}}>
                    <IonIcon color="dark" icon={closeSharp}/>
                </IonButton>
                </IonList>)}
            </IonItem>
        </>
    );
}