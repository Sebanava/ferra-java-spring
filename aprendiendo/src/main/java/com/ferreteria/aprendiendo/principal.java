package com.ferreteria.aprendiendo;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class principal {

    private List<String> usuarios_on = new ArrayList<>();
    private List<String> contraseña_on = new ArrayList<>();
    private List<String> productos = new ArrayList<>(Arrays.asList(
        "Martillo",
        "Destornilladores",
        "Tornillos",
        "Clavos",
        "Taladro",
        "Pintura",
        "Brocha"
    ));
    private List<String> carrito = new ArrayList<>();
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("productos", productos);
        model.addAttribute("carrito", carrito);
        return "productos";
    }
    
    @GetMapping("/inicio_sesion")
    public String inicio() {
        return "inicio_sesion";
    }
    
    @PostMapping("/inicio_sesion")
    public String inicio_sesion(@RequestParam(required = false) String usuario,
                               @RequestParam(required = false) String contraseña, Model model) {
        String mensaje = "";
        boolean iniciado = false;
        
        if (usuario == null || usuario.trim().isEmpty() || 
            contraseña == null || contraseña.trim().isEmpty()) {
            mensaje = "Error! Todos los campos son obligatorios";
        } else {
            int posicion = usuarios_on.indexOf(usuario.trim());
            if (posicion == -1) {
                mensaje = "Usuario no encontrado";
            } else {
                String contraseña_correcta = contraseña_on.get(posicion);
                if (passwordEncoder.matches(contraseña.trim(), contraseña_correcta)) {
                    mensaje = "Usuario iniciado";
                    iniciado = true;
                } else {
                    mensaje = "Contraseña incorrecta";
                }
            }
        }
        
        model.addAttribute("mensaje", mensaje);
        model.addAttribute("iniciado", iniciado);
        model.addAttribute("usuarioIniciado", iniciado);
        
        if (iniciado) {
            return "redirect:/productos";
        } else {
            return "inicio_sesion";
        }
    }
        
    @GetMapping("/crear_usuario")
    public String crear_usuario() {
        return "crear_usuario";
    }

    @PostMapping("/c_usuario")
    public String crear(@RequestParam(required = false) String usuario,  
                      @RequestParam(required = false) String contraseña, Model model) {
       String mensaje = "";
       boolean crear_usuario = false;
       
       if (usuario == null || usuario.trim().isEmpty() || 
           contraseña == null || contraseña.trim().isEmpty()) {
           mensaje = "Todos los campos son obligatorios";
       } else if (usuarios_on.contains(usuario.trim())) {
           mensaje = "Nombre de usuario ocupado";
       } else if (usuario.length() < 5) {
            mensaje = "El nombre de usuario debe tener como minimo 5 caracteres";
        } else if (contraseña.length() < 7) {
            mensaje = "Por seguridad la contraseña debe tener un minimo de 7 caracteres";
        } else {
            usuarios_on.add(usuario.trim());
            String contraseñaHasheada = passwordEncoder.encode(contraseña.trim());
            contraseña_on.add(contraseñaHasheada);
            mensaje = "Usuario creado";
            crear_usuario = true;
        }
       
       model.addAttribute("mensaje", mensaje);
       model.addAttribute("crear_usuario", crear_usuario);
       model.addAttribute("creado", crear_usuario);
       
       return "crear_usuario";
    }

    @GetMapping("/productos")
    public String mostrarProductos(Model model) {
        model.addAttribute("productos", productos);
        model.addAttribute("carrito", carrito);
        return "productos";
    }

    @PostMapping("/agregar")
    public String agregarProducto(@RequestParam String producto) {
        carrito.add(producto);
        return "redirect:/productos";
    }

    @PostMapping("/eliminar")
    public String eliminarProducto(@RequestParam String producto) {
        carrito.remove(producto);
        return "redirect:/carrito";
    }
    
    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        model.addAttribute("carrito", carrito);
        return "carrito";
    }
    
    @PostMapping("/pago")
    public String pago() {
        return "pago";
    }
}