import React, {useEffect, useState} from "react";
import {
    IonButton,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardTitle,
    IonCol,
    IonContent,
    IonGrid, IonInfiniteScroll, IonInfiniteScrollContent,
    IonList,
    IonLoading,
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

const log = getLogger("Home");

export const Home: React.FC<RouteComponentProps> = ({history}) => {
    const {state, createRoom, hideCreateRoom, showCreateRoom, nextPage} = useHome();
    const [disableInfiniteScroll, setDisableInfiniteScroll] = useState<boolean>(false);
    const {
        rooms, user, recentFiles, showAddRoom, creatingError, creating, fetchingRoomsError, fetchingRooms,
        fetchingRecentFilesError, fetchingRecentFiles, fetchingUser, fetchingUserError, hasMoreNotifications
    } = state;
    useEffect(() => {
        setDisableInfiniteScroll(!hasMoreNotifications);
    }, [hasMoreNotifications]);

    return (
        <IonContent>
            <div className="flex-page">
                <Header/>
                <IonPopover
                    isOpen={showAddRoom}
                    cssClass='create-room-popover'
                    backdropDismiss={false}>
                    <CreateRoom creating={creating} createRoom={createRoom} createRoomError={creatingError}
                                hide={hideCreateRoom}/>
                </IonPopover>
                <IonGrid class="grid-page ion-padding-bottom">
                    <IonRow class="fullscreen">
                        <IonCol class="fullscreen" size="3.5">
                            <div className="first-col">
                                <IonCard class="user">
                                    <User fullName={user.fullName} email={user.email} userName={user.userName}/>
                                    <IonCardContent>
                                        <IonLoading isOpen={fetchingUser} message="Fetching user"/>
                                        {fetchingUserError &&
                                        <div className="create-room-error">{fetchingUserError.message}</div>}
                                    </IonCardContent>
                                </IonCard>
                                <IonCard class="rooms">
                                    <IonCardHeader>
                                        <IonCardTitle>Your Rooms</IonCardTitle>
                                    </IonCardHeader>
                                    <IonCardContent>
                                        {rooms && (<IonList class="list-size">
                                            {rooms.map(({id, name}) => <Room onView={() => {
                                                history.push(`/room/${id}`)
                                            }} name={name} id={id} key={id}/>)}
                                        </IonList>)}
                                        <IonLoading isOpen={fetchingRooms} message="Fetching rooms"/>
                                        {fetchingRoomsError &&
                                        <div className="create-room-error">{fetchingRoomsError.message}</div>}
                                    </IonCardContent>
                                </IonCard>
                                <div className="button-div">
                                    <IonButton color="secondary" onClick={() => showCreateRoom()}>
                                        Create room</IonButton>
                                </div>
                            </div>
                        </IonCol>
                        <IonCol class="fullscreen" size="8.5">
                            <IonCard class="fullscreen">
                                <IonCardHeader>
                                    <IonCardTitle>Recent files</IonCardTitle>
                                </IonCardHeader>
                                <IonCardContent>
                                    {recentFiles && (<IonList>
                                        {recentFiles.map(({fileId, fileName, roomName, userName, time, id, roomId}) =>
                                            <Notification fileId={fileId} fileName={fileName} roomName={roomName}
                                                          userName={userName}
                                                          time={time} id={id} roomId={roomId} onView={() => {
                                                history.push(`/room/${roomId}`)
                                            }}/>)}
                                    </IonList>)}
                                    <IonLoading isOpen={fetchingRecentFiles} message="Fetching recent files"/>
                                    {fetchingRecentFilesError &&
                                    <div className="create-room-error">{fetchingRecentFilesError.message}</div>}
                                    <Notification fileId="" fileName="File1" roomName="Room 1" userName="User1"
                                                  time={new Date(Date.now())} id="1" roomId="0" onView={() => {
                                        history.push(`/room/0`)
                                    }}/>
                                    <Notification fileId="" fileName="File2" roomName="Room 1" userName="User2"
                                                  time={new Date(Date.now())} id="2" roomId="1" onView={() => {
                                        history.push(`/room/1`)
                                    }}/>
                                    <IonInfiniteScroll threshold="100px" disabled={disableInfiniteScroll}
                                                       onIonInfinite={(e: CustomEvent<void>) => searchNext(e)}>
                                        <IonInfiniteScrollContent
                                            loadingText="Loading more posts...">
                                        </IonInfiniteScrollContent>
                                    </IonInfiniteScroll>
                                </IonCardContent>
                            </IonCard>
                        </IonCol>
                    </IonRow>
                </IonGrid>
            </div>
        </IonContent>
    );

    async function searchNext(e: CustomEvent<void>) {
        log("searchNext");
        await nextPage?.();
        (e.target as HTMLIonInfiniteScrollElement).complete();
    }
}

