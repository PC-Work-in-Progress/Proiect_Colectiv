import { IonCardHeader, IonCardSubtitle, IonCardTitle } from "@ionic/react";
import React from "react";
import { FileProps } from "./FileProps";

export const MyFile: React.FC<FileProps> = ({fileId, name, username, type, date, tags}) => {
    return (
        <>
            <IonCardHeader>
                <div className="file-div">
                    <div className="file-info">
                        <IonCardTitle class="file-name">{name}</IonCardTitle>
                        <IonCardSubtitle class="file-full-name">{username}</IonCardSubtitle>
                        <div className="file-name">{tags}</div>
                    </div>
                </div>
            </IonCardHeader>
        </>
    );
}