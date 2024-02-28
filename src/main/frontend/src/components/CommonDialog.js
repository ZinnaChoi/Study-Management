import React, { useState, useEffect } from "react";
import "../styles/Dialog.css";
import "../styles/Button.css";
import {
  Button,
  TextField,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import useMediaQuery from "@mui/material/useMediaQuery";

export default function CommonDialog(props) {
  const [internalOpen, setInternalOpen] = useState(false);
  const isControlled = props.open !== undefined;
  const open = isControlled ? props.open : internalOpen;
  const closeAfterSubmit = props.closeDialog == false ? false : true;
  const theme = useTheme();
  const fullScreen =
    useMediaQuery(theme.breakpoints.down("sm")) || props.fullScreen;

  const handleOpen = () => {
    if (!isControlled) {
      setInternalOpen(true);
    }
  };

  const handleClose = (event, reason) => {
    if (reason && reason === "backdropClick") {
      return;
    }
    if (!isControlled) {
      setInternalOpen(false);
    }
    if (props.onClose) {
      props.onClose(event, reason);
    }
  };
  useEffect(() => {
    if (isControlled) {
      setInternalOpen(props.open);
    }
  }, [props.open, isControlled]);

  return (
    <React.Fragment>
      {props.showButton && props.btnTitle && (
        <Button
          variant="outlined"
          className={props.btnClass}
          onClick={handleOpen}
        >
          {props.btnTitle}
        </Button>
      )}
      <Dialog
        fullScreen={fullScreen}
        open={open}
        onClose={handleClose}
        PaperProps={{
          component: "form",
          onSubmit: (event, reason) => {
            event.preventDefault();
            const formData = new FormData(event.currentTarget);
            const formJson = Object.fromEntries(formData.entries());
            props.submitEvt(formJson, event);
            closeAfterSubmit && handleClose(event, reason);
          },
        }}
      >
        <DialogTitle className="dialog-title">{props.title}</DialogTitle>
        <hr className="title-under-line" />

        <DialogContent>
          {props.names &&
            props.names.map((name, index) => (
              <div className="dialog-content" key={index}>
                <div className="input-key">{name}</div>
                <TextField
                  required={props.isRequireds && props.isRequireds[index]}
                  margin="dense"
                  id={name}
                  name={name}
                  value={props.defaultValues && props.defaultValues[index]}
                  helperText={props.descriptions && props.descriptions[index]}
                  type={props.inputTypes && props.inputTypes[index]}
                  variant="outlined"
                  className="dialog-input"
                  sx={{
                    width: "65%",
                    "& .MuiInputBase-root": {
                      height: 32,
                    },
                  }}
                />
              </div>
            ))}
          {props.extraComponents}
        </DialogContent>

        <DialogActions className="action-btn">
          {props.acceptStr && (
            <Button className="accept-btn" type="submit">
              {props.acceptStr}
            </Button>
          )}
          {props.cancleStr && (
            <Button className="cancel-btn" onClick={handleClose}>
              {props.cancleStr}
            </Button>
          )}
        </DialogActions>
      </Dialog>
    </React.Fragment>
  );
}
