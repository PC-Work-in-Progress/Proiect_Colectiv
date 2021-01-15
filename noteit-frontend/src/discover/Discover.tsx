import React, {useEffect, useState} from "react";
import {
    IonButton,
    IonCard,
    IonCardContent,
    IonCardHeader,
    IonCardTitle,
    IonCol,
    IonContent,
    IonGrid,
    IonList,
    IonRow,
    IonSearchbar
} from '@ionic/react';
import {Header} from "../layout/Header";
import {getLogger} from "../shared";
import {RouteComponentProps} from "react-router";
import "./Discover.css"
import {TagProps, useDiscover} from "./useDiscover";
import {Room} from "./Room";
import {Tag} from "./Tag";

const log = getLogger("Discover");

export interface TagCheck extends TagProps {
    checked: boolean;
}

const initialState: TagCheck[] = []


export const Discover: React.FC<RouteComponentProps> = ({history}) => {
    const {state, filterRooms, searchRooms} = useDiscover();
    const [tagCheck, setTagCheck] = useState(initialState);
    const [searchText, setSearchText] = useState("");
    const {rooms, fetchingRoomsError, fetchingRooms, tags} = state;

    useEffect(initializeTagState, [tags]);

    return (
        <IonContent class="fullscreen">
            <div className="flex-page">
                <Header/>
                <IonGrid class="grid-page ion-padding-bottom">
                    <IonRow class="fullscreen">
                        <IonCol class="fullscreen" offset="0.5" size="3">
                            <IonCard>
                                <IonCardHeader>
                                    <IonCardTitle>Filter</IonCardTitle>
                                </IonCardHeader>
                                <IonCardContent>
                                    {tagCheck && (<IonList>
                                        {tagCheck.map(({id, name, checked}) => <Tag
                                            onCheck={() => {
                                                log("changeCheck");
                                                const index = tagCheck.findIndex(t => t.id === id);
                                                tagCheck[index].checked = !tagCheck[index].checked;
                                                setTagCheck(tagCheck);
                                            }}
                                            checked={checked}
                                            name={name} id={id} key={id}/>)}
                                    </IonList>)}
                                    <div className="filter-button"><IonButton onClick={() => {
                                        filterByTags();
                                    }}>Filter</IonButton></div>
                                </IonCardContent>
                            </IonCard>
                        </IonCol>
                        <IonCol class="fullscreen" size="8">
                            <IonSearchbar placeholder="Room name" value={searchText}
                                          onIonChange={e => {
                                              setSearchText(e.detail.value || '');
                                              // changeSearch(e.detail.value!);
                                          }}
                                          onKeyPress={e => {
                                              if (e && e.key === "Enter") {
                                                  searchRooms(searchText);
                                              }
                                          }}
                            />
                            <IonCard class="room-list-card">
                                <IonCardContent>
                                    <IonList class="room-list">
                                        {rooms.length > 0 && (
                                            rooms
                                                .map(({id, name}) =>
                                                    <Room key={id}
                                                          id={id}
                                                          name={name}
                                                          onView={() => {
                                                              history.push(`/room/${id}`)
                                                          }}/>)
                                        )}
                                    </IonList>
                                </IonCardContent>
                            </IonCard>
                        </IonCol>
                    </IonRow>
                </IonGrid>
            </div>
        </IonContent>
    );

    function initializeTagState() {
        let result: TagCheck[] = [];
        tags.forEach(t => {
            result.push({id: t.id, name: t.name, checked: false});
        })
        setTagCheck(result);
    }

    function filterByTags() {
        const tagList: string[] = [];
        tagCheck.forEach(t => {
            if (t.checked) {
                tagList.push(t.name);
            }
        })
        filterRooms(tagList);
    }
}

