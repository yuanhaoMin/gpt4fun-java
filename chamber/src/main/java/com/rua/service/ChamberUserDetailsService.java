package com.rua.service;

import com.rua.ChamberUserPrincipal;
import com.rua.entity.ChamberUser;
import com.rua.logic.ChamberUserLogic;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChamberUserDetailsService implements UserDetailsService {

    private final ChamberUserLogic chamberUserLogic;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        ChamberUser chamberUser = chamberUserLogic.findByUsername(username);
        return new ChamberUserPrincipal(chamberUser);
    }

}