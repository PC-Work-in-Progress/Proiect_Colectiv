import { IonCardHeader, IonCardSubtitle, IonCardTitle } from "@ionic/react";
import React from "react";
import { FileProps } from "./FileProps";

export const MyFile: React.FC<FileProps> = ({    fileId,name,type,username,date,URL,size}) => {
    return (
        <>
            <IonCardHeader>
                <div className="file-div">
                    <div className="file-info">
                        <IonCardTitle class="file-name">{name}</IonCardTitle>
                        <IonCardSubtitle class="file-full-name">{"Posted by " + username}</IonCardSubtitle>
                        <div className="file-name">{date}</div>
                    </div>
                </div>
            </IonCardHeader>
        </>
    );
}