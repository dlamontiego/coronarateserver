package com.example.coronarateserver.basicAuth;

import org.springframework.stereotype.Service;

@Service
public class VerifyUser {
    public boolean verifyUserAndPassword(LoginForm lf, boolean isCurrency) {
        if (isCurrency){
            return lf.getUsername().equals("ratesCurrency") && lf.getPassword().equals("zLNCMt3248dhoa4aonhr7we8s378jJZikuEr2cbIQfajMT4SnAEKOxnmmzStjZQ4yjocb2vIX0xRuekOQ65W8epPNWoPbk");
        } else {
            return lf.getUsername().equals("coronabackup") && lf.getPassword().equals("zLNCMtXRoUfHjaWCLcKwy0wnZQlBmQ8eNLAhU5nYDDvKOdjmjJZikuEr2cbIQfajMT4SnAEKOxnmmzStjZQ4yjocb2vIX0xRuekOQ65W8epPNWoPbk");
        }
    }
}
