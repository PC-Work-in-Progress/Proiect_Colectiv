import React, {useState} from "react";
import {
    IonButton,
    IonButtons,
    IonContent,
    IonIcon,
    IonInput,
    IonItem,
    IonLoading,
    IonTitle,
    IonToolbar
} from '@ionic/react';
import "./recentfiles/Notification.css"
import "./Home.css"
import {CreateRoomFn} from "./useHome";
import {getLogger} from "../shared";
import {closeSharp} from "ionicons/icons";

export type HideRoomFn = () => void;

interface CreateRoomProps {
    createRoom: CreateRoomFn;
    createRoomError?: Error | null;
    creating: boolean,
    hide: HideRoomFn
}

const log = getLogger("CreateRoom");

export const CreateRoom: React.FC<CreateRoomProps> = ({createRoom, createRoomError, creating, hide}) => {
    const [name, setName] = useState("");
    log("render");
    return (
        <IonContent class="popover-content">
            <IonToolbar>
                <IonTitle>Create room</IonTitle>
                <IonButtons slot="end"><IonButton onClick={() => hide()}><IonIcon
                    icon={closeSharp}/></IonButton></IonButtons>
            </IonToolbar>
            <div className="ion-padding-start ion-padding-bottom">
                <IonItem class="popover-room-name" lines="none">
                    <IonInput placeholder="Room name" value={name} onIonChange={e => setName(e.detail.value || '')}
                              class="room-name-input"/>
                </IonItem>
                <div className="button-div">
                    <IonButton color="secondary" onClick={() => {
                        createRoom(name);
                    }}>Create</IonButton>
                </div>
                <IonLoading isOpen={creating} message="Creating room"/>
                {createRoomError && <div className="create-room-error">{createRoomError.message}</div>}
            </div>
        </IonContent>
    );
}

