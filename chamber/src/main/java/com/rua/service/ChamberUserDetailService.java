package com.rua.service;

import com.rua.entity.ChamberUser;
import com.rua.logic.ChamberUserLogic;
import com.rua.model.ChamberUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChamberUserDetailService implements UserDetailsService {

    private final ChamberUserLogic chamberUserLogic;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ChamberUser chamberUser = chamberUserLogic.findUserByUsername(username);
        return new ChamberUserPrincipal(chamberUser);
    }

}