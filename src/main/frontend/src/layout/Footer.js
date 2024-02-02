import React from "react";
import { Typography, Container } from "@mui/material";
import { copyRights } from "../constants/constants";

// Footer에 해당
const Footer = () => {
  return (
    <React.Fragment>
      <Container maxWidth="lg">
        <hr />
        <footer>
          <Typography
            variant="subtitle2"
            align="right"
            color="textSecondary"
            component="p"
          >
            {copyRights}
          </Typography>
        </footer>
      </Container>
    </React.Fragment>
  );
};

export default Footer;
