import React from "react";
import {Link} from "react-router-dom";
import AppBar from "@material-ui/core/AppBar";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import Button from "@material-ui/core/Button";
import {theme} from "./theme";
import makeStyles from "@material-ui/core/styles/makeStyles";

const useStyles = makeStyles({
    link: {
        textDecoration: "none",
        color: theme.palette.secondary.light
    },
    toolbar: {
        display: "flex",
        justifyContent: "space-between"
    },
    secondaryDark: {
        background: theme.palette.secondary.dark
    },
    appName: {
        textTransform: "none"
    }
});

export const Header = () => {
    const classes = useStyles();
    return (
        <AppBar className={classes.secondaryDark} position="sticky">
            <Toolbar className={classes.toolbar}>
                <Button><Link edge="end" className={classes.link} to="/"><Typography className={classes.appName}
                                                                                     variant="h6">
                    NoteIt
                </Typography></Link></Button>
                <div>
                    <Button><Link className={classes.link} to="/login">Login</Link></Button>
                    <Button><Link className={classes.link} to="/login">Sign up</Link></Button>
                </div>
            </Toolbar>
        </AppBar>
    );
}

