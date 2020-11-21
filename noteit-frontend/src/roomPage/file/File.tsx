import { IonCardHeader, IonCardSubtitle, IonCardTitle, IonLabel } from "@ionic/react";
import React from "react";
import { FileProps } from "./FileProps";

export const MyFile: React.FC<FileProps> = ({fileId, name, userName, type, date, tags}) => {
    return (
        <>
            <IonCardHeader>
                <div className="file-div">
                    <div className="file-info">
                        <IonCardTitle class="file-name">{"Filename: " + name}</IonCardTitle>
                        <IonCardSubtitle class="file-full-name">
                            <IonLabel>{"Uploaded by " + userName + " on " + date.toLocaleString().substr(0,11)+ ".File type is: " +  type} </IonLabel>
                        </IonCardSubtitle>
                        <div className="file-name">{"List of tags: " + tags}</div>
                    </div>
                </div>
            </IonCardHeader>
        </>
    );
}