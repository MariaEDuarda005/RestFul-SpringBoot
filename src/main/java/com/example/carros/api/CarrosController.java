package com.example.carros.api;

import com.example.carros.domain.Carro;
import com.example.carros.domain.CarroService;
import com.example.carros.domain.dto.CarroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController

@RequestMapping("/api/v1/carros")
public class CarrosController {

    // COM A CLASSE EXCEPTION, AQUI NÃO FAZEMOS TRATAÇÃO DE METODO NENHUM

//    private CarroService service = new CarroService();

    //marca um ponto de injeção de dependência. @Autowired, faz a ligação de um bean que é apropriado para esse ponto de injeção.
    @Autowired
    private CarroService service;

    @GetMapping()
    public ResponseEntity<List<CarroDTO>> get() {
        //return service.getCarros();
        return new ResponseEntity<>(service.getCarros(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity get(@PathVariable("id") Long id ) {

        CarroDTO carro = service.getCarroById(id);

        return ResponseEntity.ok(carro);

//        return carro
//                .map(c -> ResponseEntity.ok(c)) // ve se ele existe com a função map
//                .orElse(ResponseEntity.notFound().build()); // caso contrario, not found

        // outros modos de fazer

        // utilizando o if ternario
//        return carro.isPresent() ?
//                ResponseEntity.ok(carro.get()) :
//                ResponseEntity.notFound().build();

//        if (carro.isPresent()){
//            Carro c = carro.get();
//            return ResponseEntity.ok(c);
//        } else {
//            //.build() é um método utilizado principalmente para construir e configurar uma aplicação
//            return ResponseEntity.notFound().build();
//        }
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<CarroDTO>> getCarrosByTipo(@PathVariable("tipo") String tipo ) {
        List<CarroDTO> carros = service.getCarrosByTipo(tipo);

        // se a lista estiver vazia retorna um noContent
        return carros.isEmpty() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.ok(carros);
    }

    @PostMapping
    public ResponseEntity post(@RequestBody Carro carro){

        CarroDTO c = service.insert(carro);

        URI location = getUri(c.getId());
        return ResponseEntity.created(location).build();

//        try{
//            CarroDTO c = service.insert(carro);
//
//            URI location = getUri(c.getId());
//            return ResponseEntity.created(location).build();
//        } catch (Exception ex) {
//            return ResponseEntity.badRequest().build();
//        }
    }

    private URI getUri(Long id){
        //montando a url ate o caminho barra id para este novo recurso
        return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(id).toUri();
    }

    @PutMapping("/{id}")
    public ResponseEntity put(@PathVariable("id") Long id, @RequestBody Carro carro){
        CarroDTO c = service.update(carro, id);
        return c != null ?
                ResponseEntity.ok(c) :
                ResponseEntity.notFound().build();
//        if (c != null) {
//            return ResponseEntity.ok(c);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
    }



    // dois modos de realizar o delete
//    @DeleteMapping("/{id}")
//    public ResponseEntity delete(@PathVariable("id") Long id){
//
//        boolean ok = service.delete(id);
//
//        return ok ?
//                ResponseEntity.ok().build() :
//                ResponseEntity.notFound().build();
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        service.delete(id);

        return ResponseEntity.ok().build();
    }
}
