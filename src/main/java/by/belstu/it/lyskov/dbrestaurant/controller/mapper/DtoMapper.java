package by.belstu.it.lyskov.dbrestaurant.controller.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class DtoMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public <D> D map(Object source, Class<D> destinationClass) {
        return modelMapper.map(source, destinationClass);
    }

    public <D> List<D> mapAll(Collection<?> sourceCollection, Class<D> destinationClass) {
        return sourceCollection.stream().map(e -> modelMapper.map(e, destinationClass)).toList();
    }

    public <S, D, V> void addTypeMapping(Class<S> sourceType, Class<D> destinationType,
                                         org.modelmapper.spi.SourceGetter<S> sourceGetter,
                                         org.modelmapper.spi.DestinationSetter<D, V> destinationSetter) {
        modelMapper.typeMap(sourceType, destinationType).addMapping(sourceGetter, destinationSetter);
    }
}