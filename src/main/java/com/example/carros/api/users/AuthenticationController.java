package com.example.carros.api.users;

import com.example.carros.api.infra.security.TokenService;
import com.example.carros.api.users.dtos.AuthenticationDTO;
import com.example.carros.api.users.dtos.LoginResponseDTO;
import com.example.carros.api.users.dtos.RegisterDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth") // sempre que tiver o endpoint auth vai cair nesse authentication
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    // RequestBody - recebe as informações no corpo da requisição
    public ResponseEntity<?> login(@RequestBody @Valid AuthenticationDTO data){
        try {
            // não é uma boa pratica salvar a senha do usuario como uma string , padrão é fazer um rest
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.senha());
            System.out.print(usernamePassword);
            var auth = this.authenticationManager.authenticate(usernamePassword);
            // o parameto do authentication é o usuario e a senha juntos
            // pega a senha que o usuario inseriu criptografa ela e compara com a senha criptografada do banco de dados

            var token = tokenService.generateToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO data){
        if (this.repository.findByLogin(data.login()) != null) return ResponseEntity.badRequest().build();

        // caso não encontre ninguem com esse login pode fazer o registro no banco
        String encrytedPassword = new BCryptPasswordEncoder().encode(data.senha()); // hash da senha
        User newUser = new User(data.nome(), data.login(), encrytedPassword, data.role());

        this.repository.save(newUser);
        return ResponseEntity.ok().build();
    }
}
