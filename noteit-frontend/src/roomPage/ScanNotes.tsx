import { IonButton, IonContent, IonInput, IonItem, IonLabel, IonTextarea } from "@ionic/react";
import React, { useContext, useEffect } from "react";
import { useState } from "react";
import { ScanNotesFn, UploadFileFn } from "./useRoomPage";
import {scanNotes, scanNotes as scanNotesApi} from "./roomApi"
import { AuthContext } from "../auth/AuthProvider";
import "./ScanNotes.css"

export type HideRoomFn = () => void;

interface ScanNotesProps {
    uploadFile: UploadFileFn;
    file: FormData | undefined;
    text?: string;
    roomId: string;
    hide: HideRoomFn;
    tags: string[];
}




export const ScanNotes: React.FC<ScanNotesProps> = ({uploadFile, file, roomId, hide, tags}) => {
    const [text, setText] = useState("Waiting for file to be analysed");
    const [state, setFileState] = useState<ScanNotesProps>();
    const [filename, setFilename] = useState("Scanned Notes");
    const {token} = useContext(AuthContext);
    useEffect(scanNotesEffect,[file]);

    function sendFile(text: string) {

        var formData = new FormData();
        var blob = new Blob([text], { type: "text/xml"});

        let stringTags = '';
        for (let i = 0; i < tags.length; i++) {
            stringTags += tags[i];
            if (i !== tags.length - 1) {
                stringTags += ',';
            }
        }
       

        formData.append("file", blob, filename);

        uploadFile(formData, roomId, stringTags);
        hide();
    };
     

    function scanNotesEffect() {
        let canceled = false;
        scanNotesAsync();
        return () => {
            canceled = true;
        }

        async function scanNotesAsync() {
            if(file) {
                await scanNotes(token, file).then(result => {
                    setText(result)
                });
                
            }
        }
    }
    return (
        <IonContent class="popover-content">

            <IonTextarea className="textAreaScanNotes" value={text} onIonChange={e => setText(e.detail.value!)} autoGrow = { true }></IonTextarea>
            <div className="ion-padding-start ion-padding-bottom">    
                <IonLabel>Filename:</IonLabel>
                <IonInput   value={filename}
                        onIonChange={e => setFilename(e.detail.value!)}/>
                <div className="button-div">
                
                <IonButton  onClick={() => sendFile(text)}> Upload </IonButton>
                </div>
                <div className="button-div">
                <IonButton  onClick={() => hide()}> Cancel </IonButton>
                </div>
            </div>
        </IonContent>
    );
}

