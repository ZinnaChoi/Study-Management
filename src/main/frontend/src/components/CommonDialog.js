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

export default function CommonDialog(props) {
  const [internalOpen, setInternalOpen] = useState(false);
  const isControlled = props.open !== undefined;
  const open = isControlled ? props.open : internalOpen;

  const handleOpen = () => {
    if (!isControlled) {
      setInternalOpen(true);
    }
  };

  const handleClose = (reason) => {
    if (reason && reason === "backdropClick") {
      return;
    }
    if (!isControlled) {
      setInternalOpen(false);
    }
    if (props.onClose) {
      props.onClose();
    }
  };

  useEffect(() => {
    if (isControlled) {
      setInternalOpen(props.open);
    }
  }, [props.open, isControlled]);

  return (
    <React.Fragment>
      {props.btnTitle && (
        <Button variant="outlined" onClick={handleOpen}>
          {props.btnTitle}
        </Button>
      )}

      <Dialog
        open={open}
        onClose={handleClose}
        PaperProps={{
          component: "form",
          onSubmit: (event) => {
            event.preventDefault();
            const formData = new FormData(event.currentTarget);
            const formJson = Object.fromEntries(formData.entries());
            props.submitEvt(formJson);
            handleClose();
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
                  helperText={props.descriptions && props.descriptions[index]}
                  type={props.inputTypes && props.inputTypes[index]}
                  variant="outlined"
                  className="dialog-input"
                  sx={{
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
