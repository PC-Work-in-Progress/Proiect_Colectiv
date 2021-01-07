import React from "react";
import {
    IonButton,
    IonButtons,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardTitle,
    IonCol,
    IonContent,
    IonGrid,
    IonIcon,
    IonList, IonLoading,
    IonPopover,
    IonRow
} from '@ionic/react';
import {Header} from "../layout/Header";
import "./Home.css"
import {Room} from "./room";
import {Notification} from "./recentfiles";
import {RouteComponentProps} from "react-router";
import {useHome} from "./useHome";
import {CreateRoom} from "./CreateRoom";
import {User} from "./user/User";
import {getLogger} from "../shared";
import {chevronBack, chevronForward} from "ionicons/icons";

const log = getLogger("Home");

export const Home: React.FC<RouteComponentProps> = ({history}) => {
    const {state, createRoom, hideCreateRoom, showCreateRoom, nextPage, previousPage} = useHome();
    const {
        rooms, user, recentFiles, showAddRoom, creatingError, creating, fetchingRoomsError, fetchingRooms,
        fetchingRecentFilesError, fetchingRecentFiles, fetchingUser, fetchingUserError, hasMoreNotifications, notificationsPage, previousNotifications
    } = state;

    return (
        <IonContent class="fullscreen-home">
            <div className="flex-page-home">
                <Header/>
                <IonPopover
                    isOpen={showAddRoom}
                    cssClass='create-room-popover'
                    backdropDismiss={false}>
                    <CreateRoom creating={creating} createRoom={createRoom} createRoomError={creatingError}
                                hide={hideCreateRoom}/>
                </IonPopover>
                <IonGrid class="grid-page ion-padding-bottom">
                    <IonRow class="fullscreen-home">
                        <IonCol class="fullscreen-home" sizeSm="3.5" sizeXs="12">
                            <div className="first-col">
                                <IonCard class="user">
                                    <User fullName={user.fullName} email={user.email} userName={user.userName}/>
                                    <IonCardContent>
                                        {/*<IonLoading isOpen={fetchingUser} message="Fetching user"/>*/}
                                        {fetchingUserError &&
                                        <div className="create-room-error">{fetchingUserError.message}</div>}
                                    </IonCardContent>
                                </IonCard>
                                <div className="rooms">
                                    <IonCard>
                                        <IonCardHeader>
                                            <IonCardTitle>Your Rooms</IonCardTitle>
                                        </IonCardHeader>
                                        <IonCardContent>
                                            {rooms && (<IonList class="list-size">
                                                {rooms.map(({id, name}) => <Room onView={() => {
                                                    history.push(`/room/${id}`)
                                                }} name={name} id={id} key={id}/>)}
                                            </IonList>)}
                                            {/*<IonLoading isOpen={fetchingRooms} message="Fetching rooms"/>*/}
                                            {fetchingRoomsError &&
                                            <div className="create-room-error">{fetchingRoomsError.message}</div>}
                                        </IonCardContent>
                                    </IonCard>
                                    <div className="button-div">
                                        <IonButton color="secondary" onClick={() => showCreateRoom()}>
                                            Create room</IonButton>
                                    </div>
                                </div>
                            </div>
                        </IonCol>
                        <IonCol class="fullscreen-home" sizeSm="8.5" sizeXs="12">
                            <IonCard class="notification-list-card">
                                <IonCardHeader>
                                    <div className="recent-files-title">
                                        <IonCardTitle>Recent files</IonCardTitle>
                                        <IonButtons>
                                            <IonButton disabled={!previousNotifications}
                                                       onClick={async () => {
                                                           log("searchNext");
                                                           await previousPage?.();
                                                       }}>
                                                <IonIcon icon={chevronBack}/>
                                            </IonButton>
                                            <span>{notificationsPage}</span>
                                            <IonButton disabled={!hasMoreNotifications}
                                                       onClick={async () => {
                                                           log("searchPrevious");
                                                           await nextPage?.();
                                                       }}>
                                                <IonIcon icon={chevronForward}/>
                                            </IonButton>
                                        </IonButtons>
                                    </div>
                                </IonCardHeader>
                                <IonCardContent>
                                    <IonList class="notification-list">
                                        {recentFiles.length > 0 && (
                                            recentFiles
                                                .map(({fileId, fileName, roomName, userName, fileDate, tags, roomId}) =>
                                                    <Notification key={fileId}
                                                                  fileId={fileId}
                                                                  fileName={fileName}
                                                                  roomName={roomName}
                                                                  userName={userName}
                                                                  fileDate={fileDate}
                                                                  tags={tags}
                                                                  roomId={roomId}
                                                                  onView={() => {
                                                                      history.push(`/room/${roomId}/${fileId}`)
                                                                  }}/>)
                                                .slice(notificationsPage * 15, getRecentFilesNr())
                                        )}
                                    </IonList>
                                    {/*<IonLoading isOpen={fetchingRecentFiles} message="Fetching recent files"/>*/}
                                </IonCardContent>
                            </IonCard>
                            {fetchingRecentFilesError &&
                            <div className="create-room-error">{fetchingRecentFilesError.message}</div>}
                        </IonCol>
                    </IonRow>
                </IonGrid>
            </div>
        </IonContent>
    );

    function getRecentFilesNr() {
        log("getRecentFilesNr");
        if (notificationsPage * 15 + 15 >= recentFiles.length) {
            return recentFiles.length;
        }
        return notificationsPage * 15 + 15;
    }
}

